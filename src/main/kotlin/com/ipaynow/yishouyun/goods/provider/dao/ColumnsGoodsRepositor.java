package com.ipaynow.yishouyun.goods.provider.dao;

import com.ipaynow.yishouyun.goods.provider.entity.ColumnsGoods;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColumnsGoodsRepositor extends JpaRepository<ColumnsGoods, String> {

    boolean existsByGoodsId(String s);

    boolean existsByGoodsIdIn(List<String> ids);

    List<ColumnsGoods> findByGoodsIdIn(List<String> goodsIds);
}
