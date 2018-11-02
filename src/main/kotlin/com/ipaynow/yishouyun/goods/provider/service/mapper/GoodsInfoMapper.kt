package com.ipaynow.yishouyun.goods.provider.service.mapper

import com.ipaynow.yishouyun.goods.dto.GoodsCategoryDTO
import com.ipaynow.yishouyun.goods.dto.GoodsInfoDTO
import com.ipaynow.yishouyun.goods.dto.GoodsSkuDTO
import com.ipaynow.yishouyun.goods.provider.entity.GoodsCategory
import com.ipaynow.yishouyun.goods.provider.entity.GoodsInfo
import com.ipaynow.yishouyun.goods.provider.entity.GoodsSku
import com.ipaynow.yishouyun.goods.provider.service.mapper.decorator.GoodsInfoConverterDecorator
import com.ipaynow.yishouyun.goods.provider.service.mapper.decorator.GoodsSkuConverterDecorator
import org.mapstruct.DecoratedWith
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = [GoodsSkuMapper::class]
)
@DecoratedWith(GoodsInfoConverterDecorator::class)
interface GoodsInfoMapper {
    fun from(goodsInfo: GoodsInfo?): GoodsInfoDTO?
    fun from(goodsInfoList: List<GoodsInfo>): List<GoodsInfoDTO>

    fun to(goodsInfoDTO: GoodsInfoDTO?): GoodsInfo?
    fun to(goodsInfoDTOList: List<GoodsInfoDTO>): List<GoodsInfo>
}

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = [GoodsInfoMapper::class]
)
@DecoratedWith(GoodsSkuConverterDecorator::class)
interface GoodsSkuMapper {
    fun from(goodsSku: GoodsSku?): GoodsSkuDTO?
    fun from(goodsSkuList: List<GoodsSku>): List<GoodsSkuDTO>

    fun to(goodsSkuDTO: GoodsSkuDTO?): GoodsSku?
    fun to(goodSkuDTOList: List<GoodsSkuDTO>): List<GoodsSku>
}

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface GoodsCategoryConverter {
    fun from(goodsCategory: GoodsCategory): GoodsCategoryDTO
    fun from(godosCategoryList: List<GoodsCategory>): List<GoodsCategoryDTO>

    fun to(goodsCategoryDTO: GoodsCategoryDTO): GoodsCategory
    fun to(goodsCategoryDTOList: List<GoodsCategoryDTO>): List<GoodsCategory>
}