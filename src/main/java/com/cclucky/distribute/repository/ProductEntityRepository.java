package com.cclucky.distribute.repository;

import com.cclucky.distribute.entity.ProductEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductEntityRepository extends JpaRepository<ProductEntity, Long> {

//    @Lock(value = LockModeType.OPTIMISTIC)
//    ProductEntity save(ProductEntity productEntity);
}