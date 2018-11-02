package com.ipaynow.yishouyun.goods.provider.service.mapper

import com.ipaynow.yishouyun.goods.dto.GoodsBannerDTO
import com.ipaynow.yishouyun.goods.dto.GoodsBannerTypeDTO
import com.ipaynow.yishouyun.goods.provider.entity.GoodsBanner
import com.ipaynow.yishouyun.goods.provider.entity.GoodsBannerType
import com.ipaynow.yishouyun.goods.provider.service.mapper.decorator.GoodsBannerMapperDecorator
import org.mapstruct.DecoratedWith
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
@DecoratedWith(GoodsBannerMapperDecorator::class)
interface GoodsBannerMapper {
    fun from(goodsBanner: GoodsBanner): GoodsBannerDTO
    fun from(goodsBannerList: List<GoodsBanner>): List<GoodsBannerDTO>

    fun to(goodsBannerDTO: GoodsBannerDTO): GoodsBanner
    fun to(goodsbannerDTOList: List<GoodsBannerDTO>): List<GoodsBanner>
}

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface GoodsBannerTypeMapper {
    fun from(goodsBannerType: GoodsBannerType): GoodsBannerTypeDTO
    fun from(goodsBannerTypeList: List<GoodsBannerType>): List<GoodsBannerTypeDTO>

    fun to(goodsBannerTypeDTO: GoodsBannerTypeDTO): GoodsBanner
    fun to(goodsBannerTypeDTOList: List<GoodsBannerTypeDTO>): List<GoodsBanner>
}