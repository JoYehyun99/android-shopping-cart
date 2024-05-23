package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.db.product.ProductDao
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.utils.NoSuchDataException

class ProductRepositoryImpl(context: Context) : ProductRepository {
    private val productDao = ProductDao()

    override fun loadPagingProducts(
        offset: Int,
        pagingSize: Int,
    ): List<Product> {
        return productDao.findPagingProducts(offset, pagingSize)
    }

    override fun hasNextProductPage(
        offset: Int,
        pagingSize: Int,
    ): Boolean {
        return offset + pagingSize < productDao.getProductCount()
    }

    override fun getProduct(productId: Long): Product {
        val product = productDao.findProductById(productId)
        return product ?: throw NoSuchDataException()
    }
}
