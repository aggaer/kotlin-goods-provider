package com.ipaynow.yishouyun.goods.provider.service.mapper.decorator

import com.alibaba.fastjson.JSON
import com.ipaynow.yishouyun.goods.bean.AttributeNode
import com.ipaynow.yishouyun.goods.dto.GoodsInfoDTO
import com.ipaynow.yishouyun.goods.provider.entity.GoodsInfo
import com.ipaynow.yishouyun.goods.provider.entity.GoodsSku
import com.ipaynow.yishouyun.goods.provider.service.mapper.GoodsInfoMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

/**
 * @author jerry
 * @created on 2018/10/10
 * 在goodsInfo转化为goodsInfoDTO时
 * 将attributes属性（String）转为json数组存入goodsInfoDTO中
 */
abstract class GoodsInfoConverterDecorator : GoodsInfoMapper {
    @Autowired
    @Qualifier("delegate")
    lateinit var goodsInfoMapper: GoodsInfoMapper

    override fun from(goodsInfo: GoodsInfo?): GoodsInfoDTO? {
        if (goodsInfo == null) {
            return null
        }
        val goodsInfoDTO = goodsInfoMapper.from(goodsInfo) ?: return null
        val goodsSkuList = goodsInfo.goodsSkuList
        runBlocking {
            val deferredAttributes = async { convertAttributesTextToNodes(goodsInfo.attributeList) }
            val deferredLowestDiscountPrice = async { calLowestDiscountPriceAsync(goodsSkuList) }
            val deferredLowestsellingPrice = async { calLowestSellingPriceAsync(goodsSkuList) }
            val deferredSubImgs = async { convertSubImgsToJsonArray(goodsInfo.subImgs) }
            goodsInfoDTO.attributes = deferredAttributes.await()        //将goodsInfo中的属性列表转为json objects
            goodsInfoDTO.lowestDiscountPrice = deferredLowestDiscountPrice.await()      //设置sku列表中的最低现价
            goodsInfoDTO.lowestSellingPrice = deferredLowestsellingPrice.await()        //计算sku列表中的最低原价
            goodsInfoDTO.subImgs = deferredSubImgs.await()          //将subImgs从string转为数组；
        }
        return goodsInfoDTO
    }

    override fun from(goodsInfoList: List<GoodsInfo>): List<GoodsInfoDTO> {
        return goodsInfoList.mapNotNull { goodsInfo -> from(goodsInfo) }
    }

    override fun to(goodsInfoDTO: GoodsInfoDTO?): GoodsInfo? {
        if (goodsInfoDTO == null) {
            return null
        }
        val goodsInfo = goodsInfoMapper.to(goodsInfoDTO) ?: return null
        val attributes = goodsInfoDTO.attributes
        runBlocking {
            val deferredSubImgs = GlobalScope.async { JSON.toJSONString(goodsInfoDTO.subImgs) }
            goodsInfo.attributeList = if (attributes.isNotEmpty()) JSON.toJSONString(attributes) else null.toString()
            goodsInfo.subImgs = deferredSubImgs.await()
        }
        return goodsInfo
    }

    override fun to(goodsInfoDTOList: List<GoodsInfoDTO>): List<GoodsInfo> {
        return goodsInfoDTOList.mapNotNull { goodsInfoDTO -> to(goodsInfoDTO) }
    }

    /**
     * 计算sku列表中的最低现价
     */
    private fun calLowestDiscountPriceAsync(goodsSkuList: List<GoodsSku>): String {
        return goodsSkuList.minBy { goodsSku -> goodsSku.discountPrice }.toString()
    }

    /**
     * 计算sku列表中的最低原价
     */
    private fun calLowestSellingPriceAsync(goodsSkuList: List<GoodsSku>): String {
        return goodsSkuList.minBy { goodsSku -> goodsSku.sellingPrice }.toString()
    }

    /**
     * 将subImgs从string转为数组；
     */
    private fun convertSubImgsToJsonArray(subImgsText: String): List<String> {
        return JSON.parseArray(subImgsText, String::class.java)
    }

    //将goodsInfo中的属性列表转为json objects
    private fun convertAttributesTextToNodes(attributesText: String): List<AttributeNode> {
        return JSON.parseArray(attributesText, AttributeNode::class.java).filterNotNull()
    }
}