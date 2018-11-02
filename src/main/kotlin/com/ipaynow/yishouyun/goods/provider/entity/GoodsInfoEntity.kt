package com.ipaynow.yishouyun.goods.provider.entity

import java.math.BigDecimal
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Transient

@Entity
data class GoodsCategory(
    @Id
    var id: Long,
    var name: String,
    var type: Byte,
    var status: Int,
    var storeId: String,
    var mchId: String,
    var sortWeight: Double,
    var createTime: Instant,
    var updateTime: Instant
)

@Entity
data class GoodsInfo(
    @Id
    var id: String,
    var categoryId: Long,
    var type: Byte,
    var storeId: String,
    var merchantId: String,
    var name: String,
    var description: String,
    var code: String,
    var subImgs: String,
    var unit: String,
    var mainImg: String,
    var attributeList: String,
    var status: Int,
    var sourceId: Int?,
    var associationId: String,
    var mealPreparationTime: Byte,
    var sortWeight: Int,
    @Transient
    internal var goodsSkuList: List<GoodsSku>,
    @Transient
    var categoryName: String,
    var createTime: Instant,
    var updateTime: Instant
)

@Entity
data class GoodsSku(
    @Id
    var id: String,
    var goodsId: String,
    var merchantId: String,
    var storeId: String,
    var specification: String?,
    var specHash: Long?,
    var discountPrice: BigDecimal,
    var sellingPrice: BigDecimal,
    var weight: Double,
    var status: Int,
    var createTime: Instant,
    var updateTime: Instant
)