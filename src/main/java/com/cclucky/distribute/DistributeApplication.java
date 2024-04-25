package com.cclucky.distribute;

import com.cclucky.distribute.entity.ProductEntity;
import com.cclucky.distribute.repository.ProductEntityRepository;
import com.cclucky.distribute.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class DistributeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributeApplication.class, args);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(new JdkSerializationRedisSerializer());
        return redisTemplate;
    }

}

@RestController
class Shop {

    @Autowired
    private ProductService productService;

    @Autowired
    ProductEntityRepository productEntityRepository;

    @GetMapping("/getProductById")
    public ProductEntity getProductById(@RequestParam("id") Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/changePrice")
    public String changePrice() {
        ProductEntity productEntity = productEntityRepository.findById(1L).orElse(null);
        if (productEntity != null) {
            int currentPrice = productEntity.getPrice().intValue() + 1;
            productEntity.setPrice(new BigDecimal(currentPrice));
            System.out.println(productEntity);
            productEntityRepository.save(productEntity);
        }
        return productService.getProductById(1L).toString();
    }

    private final Lock lock = new ReentrantLock(); // 创建一个可重入锁

    @GetMapping("/changePrice2")
    public String changePrice2() {
        lock.lock(); // 加锁
        ProductEntity productEntity;
        try {
            productEntity = productEntityRepository.findById(1L).orElse(null);
            if (productEntity != null) {
                int currentPrice = productEntity.getPrice().intValue() + 1;
                productEntity.setPrice(new BigDecimal(currentPrice));
                System.out.println(productEntity);
                productEntityRepository.save(productEntity);
            }
        } finally {
            lock.unlock(); // 释放锁
        }
        assert productEntity != null;
        return productEntity.toString();
    }
}