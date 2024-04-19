package com.cclucky.distribute.service;

import com.cclucky.distribute.entity.OrderEntity;
import com.cclucky.distribute.enums.OrderStatus;
import com.cclucky.distribute.repository.OrderEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: Distribute
 * @description: OrderService
 * @author: cclucky
 * @create: 2024-04-19 14:07
 **/
@Service
public class OrderService {
    @Autowired
    private OrderEntityRepository orderRepository;

    @Transactional
    public void submitOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            // 使用数据库锁确保并发更新安全
            order = orderRepository.findByIdWithLock(orderId);
            System.out.println("修改前：" + order);
            // 更新订单状态并保存
            order.setStatus(OrderStatus.SUBMITTED.getStatus());
            System.out.println("修改后：" + order);
            System.out.println("==============");
            orderRepository.save(order);
        }
    }
}
