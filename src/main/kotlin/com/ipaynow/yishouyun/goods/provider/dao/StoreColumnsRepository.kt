package com.ipaynow.yishouyun.goods.provider.dao

import com.ipaynow.yishouyun.goods.provider.entity.ColumnsGoods
import com.ipaynow.yishouyun.goods.provider.entity.StoreColumns
import org.springframework.data.jpa.repository.JpaRepository

interface StoreColumnsRepository : JpaRepository<StoreColumns, String>

interface ColumnsGoodsRepository : JpaRepository<ColumnsGoods, String> {
    fun existsByGoodsId(s: String): Boolean

    fun findByGoodsIdIn(goodsIds: List<String>): List<ColumnsGoods>
}