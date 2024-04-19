package com.cclucky.distribute.service;

import com.cclucky.distribute.entity.ProductEntity;
import com.cclucky.distribute.repository.ProductEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: Distribute
 * @description: ProductService
 * @author: cclucky
 * @create: 2024-04-19 14:15
 **/
@Service
public class ProductService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ProductEntityRepository productRepository;

    public ProductEntity getProductById(Long productId) {
        // 先从缓存中获取商品信息
        ProductEntity product = (ProductEntity) redisTemplate.opsForValue().get("product:" + productId);
        if (product == null) {
            // 如果缓存中不存在，则从数据库中获取，并放入缓存
            product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                redisTemplate.opsForValue().set("product:" + productId, product);
            }
        }
        return product;
    }

    @Transactional
    public void updateProduct(ProductEntity product) {
        // 更新数据库中的商品信息
        product = productRepository.save(product);

        // 同步更新缓存中的商品信息
        redisTemplate.opsForValue().set("product:" + product.getId(), product);
    }
}