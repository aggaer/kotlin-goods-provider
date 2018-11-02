package repository

import com.alibaba.fastjson.JSON
import com.ipaynow.yishouyun.goods.provider.KotlinGoodsProviderApplication
import com.ipaynow.yishouyun.goods.provider.dao.ColumnsGoodsRepository
import com.ipaynow.yishouyun.goods.provider.dao.GoodsInfoRepository
import com.ipaynow.yishouyun.goods.provider.dao.GoodsSkuRepository
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner
import javax.annotation.Resource

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [KotlinGoodsProviderApplication::class])
class RepositoryTest {
    @Resource
    lateinit var goodsSkuRepository: GoodsSkuRepository
    @Resource
    lateinit var goodsInfoRepository: GoodsInfoRepository
    @Resource
    lateinit var columnsGoodsRepository: ColumnsGoodsRepository

    @Test
    fun testFindByGoodsId() {
        val optional = goodsSkuRepository.findByGoodsId("1000050006")
        Assert.assertTrue(optional.isPresent)
    }

    @Test
    fun findByStoreIdAndNameLike() {
        val page =
            goodsInfoRepository.findAllByStoreIdAndStatusAndNameIsLike("000000100537579", 0, "汉堡", PageRequest.of(0, 5))
        println(JSON.toJSONString(page, true))
    }

    @Test
    fun findById() {
        val optional = goodsInfoRepository.findById("1000050155")
        Assert.assertTrue(optional.isPresent)
    }

    @Test
    fun existByGoodsIdTest() {
        val result = columnsGoodsRepository.existsByGoodsId("1000060036")
        assert(result)
    }

    @Test
    fun deleteByIdTest() {
        val result = goodsInfoRepository.deleteByGoodsId("1000050006")
        assert(result > 0)
    }

    @Test
    fun deleteBatch() {
        val result = goodsInfoRepository.deleteInBatch(listOf("1000050006", "1000050155"))
        assert(result > 0)
    }
}