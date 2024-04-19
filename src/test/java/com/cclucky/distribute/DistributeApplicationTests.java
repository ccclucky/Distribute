package com.cclucky.distribute;

import com.cclucky.distribute.entity.ProductEntity;
import com.cclucky.distribute.repository.OrderEntityRepository;
import com.cclucky.distribute.repository.ProductEntityRepository;
import com.cclucky.distribute.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootTest
class DistributeApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void addData(
            @Autowired ProductEntityRepository productEntityRepository,
            @Autowired OrderEntityRepository orderEntityRepository,
            @Autowired OrderService orderService
            ) throws InterruptedException {
        int numThreads = 10; // 设置并发线程数
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                // 在多个线程中调用 submitOrder 方法
                System.out.println("---");
                orderService.submitOrder(1L); // 假设订单ID为1
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }

    private final Lock lock = new ReentrantLock(); // 创建一个可重入锁
    @Test
    @Transactional
    void updateData(
            @Autowired ProductEntityRepository productEntityRepository,
            @Autowired OrderEntityRepository orderEntityRepository,
            @Autowired OrderService orderService
    ) throws InterruptedException {
        int numThreads = 1000; // 设置并发线程数

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                lock.lock(); // 加锁
                try {
                    ProductEntity productEntity = productEntityRepository.findById(1L).orElse(null);
                    if (productEntity != null) {
                        int currentPrice = productEntity.getPrice().intValue() + 1;
                        productEntity.setPrice(new BigDecimal(currentPrice));
                        System.out.println(productEntity);
                        productEntityRepository.save(productEntity);
                    }
                } finally {
                    lock.unlock(); // 释放锁
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
