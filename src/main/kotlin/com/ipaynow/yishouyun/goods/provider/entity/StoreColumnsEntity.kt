package com.ipaynow.yishouyun.goods.provider.entity

import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Transient

@Entity
data class StoreColumns(
    @Id
    var id: String,
    var name: String,
    var tag: String,
    var storeId: String,
    var mchId: String,
    var status: Byte,
    @Transient
    var columnsGoodsList: List<ColumnsGoods>? = null,
    var createTime: Instant,
    var updateTime: Instant
)

@Entity
data class ColumnsGoods(
    @Id
    var id: String,
    var categoryName: String,
    var storeColumnsId: String,
    var goodsId: String,
    var sortWeight: Int,
    var status: Int,
    var createTime: Instant,
    var updateTime: Instant
)