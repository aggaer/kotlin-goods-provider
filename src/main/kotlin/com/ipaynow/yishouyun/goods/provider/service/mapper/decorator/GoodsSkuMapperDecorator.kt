package com.ipaynow.yishouyun.goods.provider.service.mapper.decorator

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.ipaynow.yishouyun.goods.dto.GoodsSkuDTO
import com.ipaynow.yishouyun.goods.provider.entity.GoodsSku
import com.ipaynow.yishouyun.goods.provider.service.mapper.GoodsSkuMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

abstract class GoodsSkuConverterDecorator : GoodsSkuMapper {
    @Autowired
    @Qualifier("delegate")
    lateinit var goodsSkuConverter: GoodsSkuMapper

    override fun from(goodsSku: GoodsSku?): GoodsSkuDTO? {
        if (goodsSku == null) {
            return null
        }
        val goodsSkuDTO = goodsSkuConverter.from(goodsSku) ?: return null
        val specification = goodsSku.specification
        goodsSkuDTO.specifications =
                if (specification.isNullOrEmpty()) null else JSON.parseObject(specification, JSONObject::class.java)
        return goodsSkuDTO
    }

    override fun from(goodsSkuList: List<GoodsSku>): List<GoodsSkuDTO> {
        return goodsSkuList.mapNotNull { goodsSku -> this.from(goodsSku) }
    }

    override fun to(goodsSkuDTO: GoodsSkuDTO?): GoodsSku? {
        if (goodsSkuDTO == null) {
            return null
        }
        val goodsSku = goodsSkuConverter.to(goodsSkuDTO) ?: return null
        val specificatioNodes = goodsSkuDTO.specifications
        goodsSku.specification = if (specificatioNodes.isNullOrEmpty()) null else specificatioNodes.toJSONString()
        return goodsSku
    }

    override fun to(goodSkuDTOList: List<GoodsSkuDTO>): List<GoodsSku> {
        return goodSkuDTOList.mapNotNull { goodsSkuDTO -> this.to(goodsSkuDTO) }
    }
}