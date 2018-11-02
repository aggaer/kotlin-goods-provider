package com.ipaynow.yishouyun.goods.provider.dao

import com.ipaynow.yishouyun.goods.provider.entity.GoodsBanner
import com.ipaynow.yishouyun.goods.provider.entity.GoodsBannerType
import org.springframework.data.jpa.repository.JpaRepository

interface GoodsbannerRepository : JpaRepository<GoodsBanner, Long>

interface GoodsBannerTypeRepository : JpaRepository<GoodsBannerType, Int>