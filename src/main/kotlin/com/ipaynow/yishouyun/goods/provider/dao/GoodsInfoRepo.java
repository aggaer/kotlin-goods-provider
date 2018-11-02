package com.ipaynow.yishouyun.goods.provider.dao;

import com.ipaynow.yishouyun.goods.provider.entity.GoodsInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsInfoRepo extends JpaRepository<GoodsInfo,String> {
    List<GoodsInfo> findByStoreIdAndCategoryIdAndStatusGreaterThan(String storeId,Long categoryId,Integer status);
}
