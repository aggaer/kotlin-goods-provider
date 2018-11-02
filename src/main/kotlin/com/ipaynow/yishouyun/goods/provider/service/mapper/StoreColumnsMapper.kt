package com.ipaynow.yishouyun.goods.provider.service.mapper

import com.ipaynow.yishouyun.goods.dto.ColumnsGoodsDTO
import com.ipaynow.yishouyun.goods.dto.StoreColumnsDTO
import com.ipaynow.yishouyun.goods.provider.entity.ColumnsGoods
import com.ipaynow.yishouyun.goods.provider.entity.StoreColumns
import com.ipaynow.yishouyun.goods.provider.service.mapper.decorator.StoreColumnsConverterDecorator
import org.mapstruct.DecoratedWith
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = [GoodsInfoMapper::class]
)
interface ColumnsGoodsMapper {
    fun from(columnsGoods: ColumnsGoods): ColumnsGoodsDTO
    fun from(columnsGoodsList: List<ColumnsGoods>): List<ColumnsGoodsDTO>

    fun to(columnsGoodsDTO: ColumnsGoodsDTO): ColumnsGoods
    fun to(columnsGoodsDTOList: List<ColumnsGoodsDTO>): List<ColumnsGoods>
}

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = [ColumnsGoodsMapper::class]
)
@DecoratedWith(StoreColumnsConverterDecorator::class)
interface StoreColumnsMapper {
    fun from(storeColumns: StoreColumns): StoreColumnsDTO
    fun from(storeColumnsList: List<StoreColumns>): List<StoreColumnsDTO>

    fun to(storeColumnsDTO: StoreColumnsDTO): StoreColumns
    fun to(storeColumnsDTOList: List<StoreColumnsDTO>): List<StoreColumns>
}