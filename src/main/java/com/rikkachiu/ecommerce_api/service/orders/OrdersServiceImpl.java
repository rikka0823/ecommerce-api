package com.rikkachiu.ecommerce_api.service.orders;

import com.rikkachiu.ecommerce_api.dao.orders.OrdersDao;
import com.rikkachiu.ecommerce_api.dao.product.ProductDao;
import com.rikkachiu.ecommerce_api.dao.user.UserDao;
import com.rikkachiu.ecommerce_api.model.dto.BuyItemDTO;
import com.rikkachiu.ecommerce_api.model.dto.OrdersDTO;
import com.rikkachiu.ecommerce_api.model.pojo.OrderItem;
import com.rikkachiu.ecommerce_api.model.pojo.Orders;
import com.rikkachiu.ecommerce_api.model.pojo.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    private static final Logger logger = LoggerFactory.getLogger(OrdersServiceImpl.class);

    @Autowired
    private OrdersDao ordersDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;

    // 創建訂單
    @Transactional
    @Override
    public Integer createOrders(Integer userId, OrdersDTO ordersDTO) {
        // 檢查 userId 是否存在
        if (userDao.getUserById(userId) == null) {
            logger.warn("userId: {} 不存在", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "查無此 userId: " + userId);
        }

        // 建立 list 儲存 OrderItem、Product
        List<OrderItem> orderItemList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();

        // 計算金額、封裝 orderItemList、productList
        int totalAmount = 0;
        for (BuyItemDTO buyItemDTO : ordersDTO.getBuyItemDTOList()) {
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
    @Override
    public Orders getOrdersById(Integer orderId) {
        // 封裝 Orders 物件
        Orders orders = ordersDao.getOrdersById(orderId);
        orders.setOrderItemList(ordersDao.getOrderItemsById(orderId));

        return orders;
    }
}
