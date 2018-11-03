package com.ipaynow.yishouyun.goods.provider.dao;

import com.ipaynow.yishouyun.goods.provider.entity.GoodsInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsInfoRepo extends JpaRepository<GoodsInfo, String> {
    List<GoodsInfo> findByStoreIdAndCategoryIdAndStatusGreaterThan(String storeId, Long categoryId, Integer status);

    Integer countByStoreIdAndCategoryId(String storeId, Long categoryId);

    //"select * from goods_info where merchant_id = #{mchId} and type = 0 and category_id = #{categoryId} and status > -1
    List<GoodsInfo> findByMerchantIdAndTypeAndCategoryIdAndStatus(String mchId, Byte type, Long categoryId, Integer status);

    //select * from goods_info where store_id = #{storeId} and status > #{status} order by sort_weight
    List<GoodsInfo> findByStoreIdAndStatusGreaterThanOrderBySortWeight(String sotreId, Integer status, Integer sortWeight);
}
