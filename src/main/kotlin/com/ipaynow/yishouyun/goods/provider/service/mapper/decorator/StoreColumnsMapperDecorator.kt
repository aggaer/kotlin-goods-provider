package com.ipaynow.yishouyun.goods.provider.service.mapper.decorator

import com.ipaynow.yishouyun.goods.dto.StoreColumnsDTO
import com.ipaynow.yishouyun.goods.provider.entity.StoreColumns
import com.ipaynow.yishouyun.goods.provider.service.mapper.StoreColumnsMapper
import com.ipaynow.yishouyun.goods.service.GoodsInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import javax.annotation.Resource

abstract class StoreColumnsConverterDecorator : StoreColumnsMapper {
    @Autowired
    @Qualifier("delegate")
    lateinit var storeColumnsConverter: StoreColumnsMapper
    @Resource
    lateinit var goodsInfoService: GoodsInfoService

    /**
     * 将store columns中的columns goods与goods info做关联查询
     * 将包含商品信息的columns goods放入store columns
     * 将store columns转化为dto
     */
    override fun from(storeColumns: StoreColumns): StoreColumnsDTO {
        val result = storeColumnsConverter.from(storeColumns)
        val goodsIdList = storeColumns.columnsGoodsList?.map { columnsGoods -> columnsGoods.goodsId }
        result.goodsInfoList = if (goodsIdList.isNullOrEmpty()) null else goodsInfoService.findByIdIn(goodsIdList).get()
        return result
    }

    override fun from(storeColumnsList: List<StoreColumns>): List<StoreColumnsDTO> {
        return storeColumnsList.map { storeColumns -> this.from(storeColumns) }
    }
}