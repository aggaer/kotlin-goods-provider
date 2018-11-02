package com.ipaynow.yishouyun.goods.provider.entity

import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class GoodsBanner(
    @Id
    var id: Long,
    var image: String,
    var jumpTypeId: Int,
    var jumpPage: String,
    var sortWeight: Int,
    var mchId: String,
    var storeId: String,
    var status: Byte,
    var createTime: Instant,
    var updateTime: Instant
)

@Entity
data class GoodsBannerType(
    @Id
    var id: Int,
    var jumpTypeName: String,
    var status: Byte,
    var createTime: Instant,
    var updateTime: Instant
)
