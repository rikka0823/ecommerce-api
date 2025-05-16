package com.rikkachiu.ecommerce_api.service.orders;

import com.rikkachiu.ecommerce_api.constant.Role;
import com.rikkachiu.ecommerce_api.dao.orders.OrdersDao;
import com.rikkachiu.ecommerce_api.dao.product.ProductDao;
import com.rikkachiu.ecommerce_api.dao.user.UserDao;
import com.rikkachiu.ecommerce_api.model.dto.BuyItemDto;
import com.rikkachiu.ecommerce_api.model.dto.OrdersDto;
import com.rikkachiu.ecommerce_api.model.dto.OrdersQueryParamsDto;
import com.rikkachiu.ecommerce_api.model.pojo.OrderItem;
import com.rikkachiu.ecommerce_api.model.pojo.Orders;
import com.rikkachiu.ecommerce_api.model.pojo.Product;
import com.rikkachiu.ecommerce_api.model.pojo.User;
import com.rikkachiu.ecommerce_api.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class OrdersServiceImpl implements OrdersService {

    private static final Logger logger = LoggerFactory.getLogger(OrdersServiceImpl.class);

    @Autowired
    private OrdersDao ordersDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    // 創建訂單
    @CacheEvict(cacheNames = "ecommerce_orders", allEntries = true)
    @Transactional
    @Override
    public Integer createOrders(Integer userId, OrdersDto ordersDto) {
        // 檢查 userId 是否存在
        existsById(userId);

        // 建立 list 儲存 OrderItem、Product
        List<OrderItem> orderItemList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();

        // 計算金額、封裝 orderItemList、productList
        int totalAmount = 0;
        for (BuyItemDto buyItemDTO : ordersDto.getBuyItemDtoList()) {
            // 檢查 productId是否存在、有足夠庫存
            Product product = productDao.getProductById(buyItemDTO.getProductId());
            if (product == null) {
                logger.warn("productId: {} 不存在", buyItemDTO.getProductId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "此 productId 不存在: " + buyItemDTO.getProductId());
            }
            if (product.getStock() < buyItemDTO.getQuantity()) {
                logger.warn("productId: {} 庫存量不足、庫存量: {}、購買量: {}",
                        buyItemDTO.getProductId(), product.getStock(), buyItemDTO.getQuantity());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "\n此 productId 庫存量不足: " + buyItemDTO.getProductId() +
                                "\n庫存量: " + product.getStock() +
                                "\n購買量: " + buyItemDTO.getQuantity());
            }

            // 計算 order_item 的 amount、訂單總金額
            int amount = buyItemDTO.getQuantity() * product.getPrice();
            totalAmount += amount;

            // 儲存至 orderItemList
            OrderItem orderItem = OrderItem.builder()
                    .productId(buyItemDTO.getProductId())
                    .quantity(buyItemDTO.getQuantity())
                    .amount(amount)
                    .build();
            orderItemList.add(orderItem);

            // 更新庫存，儲存至 productList
            product.setStock(product.getStock() - buyItemDTO.getQuantity());
            productList.add(product);
        }

        // 創建訂單
        int orderId = ordersDao.createOrders(userId, totalAmount);
        if (orderId == -1) {
            return -1;
        }

        // 批量創建商品明細
        ordersDao.createOrderItem(orderId, orderItemList);

        // 批量更新商品庫存
        productDao.updateProductStock(productList);

        return orderId;
    }

    //取得訂單
    @Cacheable(cacheNames = "ecommerce_orders", key = "'orderId-' + #p0",
            unless = "#result == null")
    @Override
    public Orders getOrdersById(Integer orderId) {
        // 封裝 Orders 物件
        Orders orders = ordersDao.getOrdersById(orderId);
        orders.setOrderItemList(ordersDao.getOrderItemsById(orderId));

        return orders;
    }

    // 查詢訂單總數
    @Cacheable(cacheNames = "ecommerce_orders", key = "'userId-' + #p0",
            unless = "#result == null")
    @Override
    public Integer getOrdersCount(Integer userId) {
        // 檢查 userId 是否存在
        existsById(userId);
        return ordersDao.getOrdersCount(userId);
    }

    // 查詢所有訂單，依不同條件
    @Cacheable(cacheNames = "ecommerce_orders",
            key = "'userId-' + #p0 + '-' + #p1.limit + '-' + #p1.offset",
            unless = "#result == null")
    @Override
    public List<Orders> getOrders(Integer userId, OrdersQueryParamsDto ordersQueryParamsDto) {
        // 檢查 userId 是否存在
        existsById(userId);

        // 封裝 List<Orders>
        List<Orders> ordersList = ordersDao.getOrders(userId, ordersQueryParamsDto);
        for (Orders orders : ordersList) {
            orders.setOrderItemList(ordersDao.getOrderItemsById(userId));
        }

        return ordersList;
    }

    // 刪除訂單
    @CacheEvict(cacheNames = "ecommerce_orders", allEntries = true)
    @Transactional
    @Override
    public void deleteOrders(Integer userId, Integer orderId, Authentication authentication, Jwt jwt) {
        // 依 email 查詢 user
        User user;
        if (authentication instanceof OidcUser oidcUser) {
            user = userService.getUserByEmail(oidcUser.getEmail());
        } else {
            user = userService.getUserByEmail(jwt.getClaimAsString("email"));
        }
        Set<Role> roleSet = user.getRoleSet();
        List<Integer> orderIds = ordersDao.getOrdersIds(user.getUserId());

        // 限制 ROLE_CUSTOMER 權限
        if (roleSet.size() == 1 && roleSet.contains(Role.ROLE_CUSTOMER)) {
            if (user.getUserId() != userId) {
                logger.warn("id: {} 未被授權刪除該訂單", user.getUserId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            if (!orderIds.contains(orderId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }

        // 封裝 orderItemList、productList
        List<OrderItem> orderItemList = ordersDao.getOrderItemsById(orderId);
        List<Product> productList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            Product product = productDao.getProductById(orderItem.getProductId());
            product.setStock(product.getStock() + orderItem.getQuantity());
            productList.add(product);
        }

        // 批量更新商品庫存
        productDao.updateProductStock(productList);

        // 依 orderId 刪除訂單
        ordersDao.deleteOrders(orderId);
    }

    // 檢查 userId 是否存在
    private void existsById(Integer userId) {
        if (userDao.getUserById(userId) == null) {
            logger.warn("userId: {} 不存在", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "查無此 userId: " + userId);
        }
    }
}
