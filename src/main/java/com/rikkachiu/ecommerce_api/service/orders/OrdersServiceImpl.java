package com.rikkachiu.ecommerce_api.service.orders;

import com.rikkachiu.ecommerce_api.dao.orders.OrdersDao;
import com.rikkachiu.ecommerce_api.dao.product.ProductDao;
import com.rikkachiu.ecommerce_api.model.dto.OrdersDTO;
import com.rikkachiu.ecommerce_api.model.pojo.OrderItem;
import com.rikkachiu.ecommerce_api.model.pojo.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersDao ordersDao;

    @Autowired
    private ProductDao productDao;

    // 創建訂單
    @Transactional
    @Override
    public Integer createOrders(Integer userId, OrdersDTO ordersDTO) {
        // 建立 list 儲存 productId、orderItem
        List<Integer> productIdList = new ArrayList<>();
        List<OrderItem> orderItemList = new ArrayList<>();

        // 計算金額、封裝 orderItemList
        int totalAmount = 0;
        for (int i = 0; i < ordersDTO.getBuyItemDTOList().size(); i++) {
            // 取得所有商品價格
            productIdList.add(ordersDTO.getBuyItemDTOList().get(i).getProductId());
            List<Integer> prices = productDao.getProductPrices(productIdList);

            // 計算 order_item 的 amount、訂單總金額
            int amount = ordersDTO.getBuyItemDTOList().get(i).getQuantity() * prices.get(i);
            totalAmount += amount;

            // 儲存至 orderItemList
            OrderItem orderItem = OrderItem.builder()
                    .productId(ordersDTO.getBuyItemDTOList().get(i).getProductId())
                    .quantity(ordersDTO.getBuyItemDTOList().get(i).getQuantity())
                    .amount(amount)
                    .build();
            orderItemList.add(orderItem);
        }

        // 創建訂單
        int orderId = ordersDao.createOrders(userId, totalAmount);
        if (orderId == -1) {
            return -1;
        }

        // 批量創建商品明細
        ordersDao.createOrderItem(orderId, orderItemList);

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
