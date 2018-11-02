package com.ipaynow.yishouyun.goods.provider.dao

import com.ipaynow.yishouyun.goods.provider.entity.GoodsCategory
import com.ipaynow.yishouyun.goods.provider.entity.GoodsInfo
import com.ipaynow.yishouyun.goods.provider.entity.GoodsSku
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*
import javax.transaction.Transactional

interface GoodsInfoRepository : JpaRepository<GoodsInfo, String> {

    fun findAllByStoreIdAndStatusAndNameIsLike(
        storeId: String,
        status: Int,
        name: String,
        pageRequest: Pageable
    ): Page<GoodsInfo>

    @Modifying
    @Transactional
    @Query(value = "update goods_info set status = -1 where id = ?1", nativeQuery = true)
    fun deleteByGoodsId(id: String): Int

    @Modifying
    @Transactional
    @Query("update GoodsInfo set status = -1 where id in (:ids)")
    fun deleteInBatch(ids: List<String>): Int

    @Modifying
    @Transactional
    @Query("update GoodsInfo set status = 1 where id in (:ids)")
    fun putOnShelvesBatch(ids: List<String>): Int

    @Modifying
    @Transactional
    @Query("update GoodsInfo set status = 1 where id = :id")
    fun putOnShelves(id: String): Int

    @Modifying
    @Transactional
    @Query("update GoodsInfo set status = 1 where id = :id")
    fun pullOffShelves(id: String): Int

    @Modifying
    @Transactional
    @Query("update GoodsInfo set status = 0 where id in (:ids)")
    fun pullOffShelvesBatch(ids: List<String>): Int

    fun findByIdIn(ids: List<String>): List<GoodsInfo>

    fun findByStoreIdAndCategoryIdAndStatusGreaterThan(
        storeId: String,
        categoryId: Long,
        status: Int
    ): List<GoodsInfo>

    fun findByStoreIdAndStatusGreaterThan(
        storeId: String,
        status: Int
    ): List<GoodsInfo>

    fun findByMerchantIdAndTypeAndStatusGreaterThan(
        mchId: String,
        type: Byte,
        status: Int,
        pageRequest: Pageable
    ): List<GoodsInfo>
}

interface GoodsSkuRepository : JpaRepository<GoodsSku, String> {

    fun findByGoodsId(goodsId: String): Optional<List<GoodsSku>>
}

interface GoodsCategoryRepository : JpaRepository<GoodsCategory, Long>