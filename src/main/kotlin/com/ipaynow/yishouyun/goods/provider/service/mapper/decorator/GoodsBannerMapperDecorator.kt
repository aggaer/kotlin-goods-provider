package com.ipaynow.yishouyun.goods.provider.service.mapper.decorator

import com.ipaynow.yishouyun.goods.dto.GoodsBannerDTO
import com.ipaynow.yishouyun.goods.provider.dao.GoodsBannerTypeRepository
import com.ipaynow.yishouyun.goods.provider.entity.GoodsBanner
import com.ipaynow.yishouyun.goods.provider.service.mapper.GoodsBannerMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import javax.annotation.Resource

abstract class GoodsBannerMapperDecorator : GoodsBannerMapper {

    @Autowired
    @Qualifier("delegate")
    lateinit var goodsBannerMapper: GoodsBannerMapper
    @Resource
    lateinit var goodsBannerTypeRepository: GoodsBannerTypeRepository

    override fun from(goodsBanner: GoodsBanner): GoodsBannerDTO {
        val goodsBannerDTO = goodsBannerMapper.from(goodsBanner)
        //获得banner图对应的跳转页面类型；
        val goodsBannerTypeOption = goodsBannerTypeRepository.findById(goodsBanner.jumpTypeId)
        if (goodsBannerTypeOption.isPresent) {
            goodsBannerDTO.jumpTypeName = goodsBannerTypeOption.get().jumpTypeName
        }
        return goodsBannerDTO
    }

    override fun from(goodsBannerList: List<GoodsBanner>): List<GoodsBannerDTO> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun to(goodsBannerDTO: GoodsBannerDTO): GoodsBanner {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun to(goodsbannerDTOList: List<GoodsBannerDTO>): List<GoodsBanner> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}