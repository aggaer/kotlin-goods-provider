package com.ipaynow.yishouyun.goods.provider.service

import com.github.pagehelper.PageInfo
import com.ipaynow.sequence.generator.api.SequenceAllocator
import com.ipaynow.yishouyun.common.BusinessException
import com.ipaynow.yishouyun.goods.bean.PageContent
import com.ipaynow.yishouyun.goods.bean.PendingResult
import com.ipaynow.yishouyun.goods.dto.GoodsInfoDTO
import com.ipaynow.yishouyun.goods.exception.GoodsException
import com.ipaynow.yishouyun.goods.exception.ResultCode
import com.ipaynow.yishouyun.goods.provider.dao.ColumnsGoodsRepository
import com.ipaynow.yishouyun.goods.provider.dao.GoodsCategoryRepository
import com.ipaynow.yishouyun.goods.provider.dao.GoodsInfoRepository
import com.ipaynow.yishouyun.goods.provider.dao.GoodsSkuRepository
import com.ipaynow.yishouyun.goods.provider.entity.GoodsInfo
import com.ipaynow.yishouyun.goods.provider.service.mapper.GoodsInfoMapper
import com.ipaynow.yishouyun.goods.provider.service.mapper.GoodsSkuMapper
import com.ipaynow.yishouyun.goods.service.GoodsInfoService
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import lombok.extern.slf4j.Slf4j
import org.springframework.data.domain.Example
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.CollectionUtils
import java.util.*
import javax.annotation.Resource

@Slf4j
@Service
class GoodsInfoServiceImpl : GoodsInfoService {

    @Resource
    lateinit var goodsInfoRepository: GoodsInfoRepository
    @Resource
    lateinit var goodsCategoryRepository: GoodsCategoryRepository
    @Resource
    lateinit var goodsInfoMapper: GoodsInfoMapper
    @Resource
    lateinit var goodsSkuRepository: GoodsSkuRepository
    @Resource
    lateinit var goodsSkuMapper: GoodsSkuMapper
    @Resource
    lateinit var sequenceAllocator: SequenceAllocator
    @Resource
    lateinit var columnsGoodsRepository: ColumnsGoodsRepository

    override fun findById(id: String): PendingResult<GoodsInfoDTO> {
        val goodsInfo = goodsInfoRepository.findById(id)
            .orElseThrow { GoodsException(ResultCode.GOODS_NOT_EXISTS, String.format("ID：%s 对应的商品不存在", id)) }
        runBlocking {
            //根据goods info中的categoryId查询category信息；
            val deferredGoodsCategory = async { goodsCategoryRepository.findById(goodsInfo.categoryId) }
            //根据goods id 查询goods sku列表
            val deferredGoodsSkuList = async { goodsSkuRepository.findByGoodsId(goodsInfo.id) }
            goodsInfo.categoryName = deferredGoodsCategory.await().get().name
            goodsInfo.goodsSkuList = deferredGoodsSkuList.await().orElseGet { Collections.emptyList() }
        }
        return PendingResult.of(goodsInfoMapper.from(goodsInfo))
    }

    override fun findByStoreIdAndNameLike(
        term: String,
        storeId: String,
        pageContent: PageContent,
        status: Int
    ): PendingResult<List<GoodsInfoDTO>> {
        val page = goodsInfoRepository.findByStoreIdAndStatusGreaterThanAndNameIsLike(
            storeId,
            status,
            term,
            PageRequest.of(pageContent.pageNum, pageContent.pageSize)
        )
        val pageInfo = PageInfo<GoodsInfo>(page.content)
        pageInfo.pageNum = page.number
        pageInfo.pageSize = page.size
        pageInfo.total = page.totalPages.toLong()
        return warpWithSkuList(pageInfo)
    }

    @Throws(GoodsException::class)
    override fun findByMchIdAndNameLike(
        term: String,
        mchId: String,
        pageContent: PageContent
    ): PendingResult<List<GoodsInfoDTO>> {
        val page = goodsInfoRepository.findByMerchantIdAndTypeAndStatusGreaterThanAndNameIsLike(
            mchId,
            0,
            -1,
            term,
            PageRequest.of(pageContent.pageNum, pageContent.pageSize)
        )
        val pageInfo = PageInfo<GoodsInfo>(page.content)
        return warpWithSkuList(pageInfo)
    }

    override fun find(goodsInfoDTO: GoodsInfoDTO): PendingResult<List<GoodsInfoDTO>> {
        return wrapToPendingResult(goodsInfoRepository.findAll(Example.of(goodsInfoMapper.to(goodsInfoDTO)!!)))
    }

    override fun find(goodsInfoDTO: GoodsInfoDTO, pageRequest: PageContent): PendingResult<List<GoodsInfoDTO>> {
        val page = goodsInfoRepository.findAll(
            Example.of(goodsInfoMapper.to(goodsInfoDTO)!!),
            PageRequest.of(pageRequest.pageNum, pageRequest.pageSize)
        )
        val pageInfo = PageInfo<GoodsInfo>(page.content)
        return warpWithSkuList(pageInfo)
    }

    /**
     * 查询所有商品条目
     *
     * @return 商品条目集合
     */
    override fun findAll(): PendingResult<List<GoodsInfoDTO>> {
        val goodsInfoList = goodsInfoRepository.findAll()
        return wrapToPendingResult(goodsInfoList)
    }

    /**
     * 查询所有商品条目，按类目名分组
     *
     * @return 商品条目集合
     */
    override fun findAllGroupByCategory(): PendingResult<List<GoodsInfoDTO>> {
        //关联category表查询category name，并入goods info list；
        val goodsInfoList = goodsInfoRepository.findAll()
        runBlocking {
            goodsInfoList.forEach {
                val deferredGoodsSku = async { goodsSkuRepository.findByGoodsId(it.id) }
                val deferredGoodsCategory = async { goodsCategoryRepository.findById(it.categoryId) }
                it.categoryName = deferredGoodsCategory.await().get().name
                it.goodsSkuList = deferredGoodsSku.await().orElseGet { listOf() }
            }
        }
        return wrapToPendingResult(goodsInfoList)
    }

    override fun findByStoreIdGroupByCategory(storeId: String): PendingResult<List<GoodsInfoDTO>> {
        //关联category表查询category name，并入goods info list；
        val goodsInfoList =
            goodsInfoRepository.findByStoreIdAndStatusGreaterThanOrderBySortWeight(storeId, 0)
        runBlocking {
            goodsInfoList.forEach {
                val deferredGoodsSku = async { goodsSkuRepository.findByGoodsId(it.id) }
                val deferredGoodsCategory = async { goodsCategoryRepository.findById(it.categoryId) }
                it.categoryName = deferredGoodsCategory.await().get().name
                it.goodsSkuList = deferredGoodsSku.await().orElseGet { listOf() }
            }
        }
        return wrapToPendingResult(goodsInfoList)
    }

    //根据商品类目id查询商品列表;
    override fun findMchGoodsByCategoryId(
        mchId: String,
        categoryId: Long,
        pageRequest: PageContent
    ): PendingResult<List<GoodsInfoDTO>> {
        val page =
            goodsInfoRepository.findByMerchantIdAndTypeAndCategoryIdAndStatusGreaterThan(
                mchId,
                0,
                categoryId,
                -1,
                PageRequest.of(pageRequest.pageNum, pageRequest.pageSize)
            )
        val pageInfo = PageInfo<GoodsInfo>(page.content)
        return warpWithSkuList(pageInfo)
    }

    @Transactional
    @Throws(GoodsException::class)
    override fun saveStoreGoodsInfo(goodsInfoDTO: GoodsInfoDTO): Boolean {
        val goodsId = sequenceAllocator.nextId()
        val target = goodsInfoMapper.to(goodsInfoDTO) ?: return false
        target.id = goodsId
        //批量保存sku记录；
        return runBlocking {
            val deferredSkusSavingResult = async { insertSkuList(goodsInfoDTO, goodsId) }
            val deferredGoodsInfoSavingResult = async { insertGoodsInfo(target) }
            (deferredGoodsInfoSavingResult.await() * deferredSkusSavingResult.await()) > 0
        }
    }

    /**
     * 插入goodsInfo之前先计算排序字段
     */
    fun insertGoodsInfo(goodsInfo: GoodsInfo): Int {
        //保存前将排序权重字段插入对象
        val currentColumns = goodsInfoRepository.countByStoreIdAndCategoryId(goodsInfo.storeId, goodsInfo.categoryId)
        goodsInfo.sortWeight = (currentColumns + 1) * 100
        goodsInfoRepository.save(goodsInfo)
        return 1
    }

    /**
     * 插入sku列表之前先分配序列号id和goodsId
     */
    fun insertSkuList(goodsInfoDTO: GoodsInfoDTO, goodsId: String): Int {
        val unsavedSkuList = goodsInfoDTO.skuList.map {
            it.id = sequenceAllocator.nextId()
            it.goodsId = goodsId
            it
        }
        goodsSkuRepository.saveAll(goodsSkuMapper.to(unsavedSkuList))
        return 1
    }

    @Transactional
    override fun updateStoreGoodsInfo(goodsInfoDTO: GoodsInfoDTO): Boolean? {
        return runBlocking {
            //分别存储goodsInfo和goodsSku
            val deferredGoodsInfoSavingResult = async { goodsInfoRepository.save(goodsInfoMapper.to(goodsInfoDTO)!!) }
            val deferredGoodsSkusSavingResult =
                async { goodsSkuRepository.saveAll(goodsSkuMapper.to(goodsInfoDTO.skuList)) }
            deferredGoodsInfoSavingResult.await()
            deferredGoodsSkusSavingResult.await()
            true
        }
    }

    /**
     * 商品下架之前先判断是否在专栏中；
     *
     * @param goodsId 待下架商品id
     */
    override fun pullOffShelves(goodsId: String): Boolean? {
        return !columnsGoodsRepository.existsByGoodsId(goodsId) && goodsInfoRepository.pullOffShelves(goodsId) > 0
    }

    override fun putOnShelves(goodsId: String): Boolean? {
        return goodsInfoRepository.putOnShelves(goodsId) > 0
    }

    override fun pullOffShelvesBatch(list: MutableList<String>): Boolean? {
        //下架前先判断这批商品是否在专栏中
        val existsGoods = columnsGoodsRepository.findByGoodsIdIn(list).map { it.goodsId }
        list.removeAll(existsGoods)
        //将不在专栏中的商品下架；
        return list.isNullOrEmpty() && goodsInfoRepository.pullOffShelvesBatch(list) > 0 && existsGoods.isNullOrEmpty()
    }

    override fun putOnShelvesBatch(list: List<String>): Boolean? {
        return goodsInfoRepository.putOnShelvesBatch(list) > 0
    }

    /**
     * 商户后台查询商品列表
     *
     * @param mchId       商户id
     * @param pageContent 分页信息
     */
    override fun findByMchId(mchId: String, pageContent: PageContent): PendingResult<List<GoodsInfoDTO>> {
        return wrapToPendingResult(
            goodsInfoRepository.findByMerchantIdAndTypeAndStatusGreaterThan(
                mchId,
                0,
                -1,
                PageRequest.of(pageContent.pageNum, pageContent.pageSize)
            ).content
        )
    }

    /**
     * 门店后台查询商品列表;
     *
     * @param storeId     门店id
     * @param pageContent 分页信息
     */
    override fun findByStoreId(storeId: String, pageContent: PageContent): PendingResult<List<GoodsInfoDTO>> {
        return wrapToPendingResult(goodsInfoRepository.findByStoreIdAndStatusGreaterThan(storeId, -1))
    }

    override fun findCountByStoreIdAndCategoryId(storeId: String, categoryId: Long): Int {
        return goodsInfoRepository.findByStoreIdAndCategoryIdAndStatusGreaterThan(storeId, categoryId, -1).size
    }

    /**
     * 查询所有商品条目(带分页和按字段排序)
     *
     * @return 商品条目集合
     */
    @Throws(BusinessException::class)
    override fun findAll(pageRequest: PageContent): PendingResult<List<GoodsInfoDTO>> {
        val page = goodsInfoRepository.findAll(PageRequest.of(pageRequest.pageNum, pageRequest.pageSize))
        val pageInfo = PageInfo<GoodsInfo>(page.content)
        pageInfo.total = page.totalPages.toLong()
        pageInfo.pageNum = page.number
        pageInfo.pageSize = page.size
        return warpWithSkuList(pageInfo)
    }

    /**
     * 根据id列表寻找商品集合
     * 根据id集合查询goods info，然后关联查询goods sku
     *
     * @param ids 待查找id列表
     */
    override fun findByIdIn(ids: List<String>): PendingResult<List<GoodsInfoDTO>> {
        val goodsInfoList = goodsInfoRepository.findByIdIn(ids).map {
            runBlocking {
                val deferredGodsSkuList = async { goodsSkuRepository.findByGoodsId(it.id) }
                val deferredGoodsCategory = async { goodsCategoryRepository.findById(it.categoryId) }
                it.goodsSkuList = deferredGodsSkuList.await().orElseGet { listOf() }
                it.categoryName = deferredGoodsCategory.await().get().name
            }
            it
        }
        return wrapToPendingResult(goodsInfoList)
    }

    override fun save(goodsInfoDTO: GoodsInfoDTO): Int? {
        goodsInfoRepository.save(goodsInfoMapper.to(goodsInfoDTO)!!)
        return 1
    }

    override fun saveAll(list: List<GoodsInfoDTO>): Int {
        goodsInfoRepository.saveAll(goodsInfoMapper.to(list))
        return 1
    }

    override fun update(goodsInfoDTO: GoodsInfoDTO): Int {
        goodsInfoRepository.save(goodsInfoMapper.to(goodsInfoDTO)!!)
        return 1
    }

    override fun deleteInBatch(list: MutableList<String>): Boolean {
        //删除前先判断这批商品是否在专栏中
        val existsGoods =
            columnsGoodsRepository.findByGoodsIdIn(list).map { columnsGoods -> columnsGoods.goodsId }
        list.removeAll(existsGoods)
        return goodsInfoRepository.deleteInBatch(list) > 0 && CollectionUtils.isEmpty(existsGoods)
    }

    override fun deleteById(id: String): Boolean? {
        return !columnsGoodsRepository.existsByGoodsId(id) && goodsInfoRepository.deleteByGoodsId(id) > 0
    }

    //将携带分页和sku信息的goods list封装成 PendingResult
    private fun warpWithSkuList(pageInfo: PageInfo<GoodsInfo>): PendingResult<List<GoodsInfoDTO>> {
        val goodsInfoList = pageInfo.list
        goodsInfoList.forEach { it.goodsSkuList = goodsSkuRepository.findByGoodsId(it.id).orElseGet { listOf() } }
        return PendingResult.of(goodsInfoMapper.from(goodsInfoList)).loadPageInfo(pageInfo)
    }

    //将商品列表封装成 PendingResult
    private fun wrapToPendingResult(goodsInfoList: List<GoodsInfo>): PendingResult<List<GoodsInfoDTO>> {
        return PendingResult
            .ofNullable { goodsInfoList }
            .orElseGet { emptyList() }
            .map { goodsInfoMapper.from(it) }
    }
}