package woowacourse.shopping.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.db.product.ProductDao
import woowacourse.shopping.domain.Product

class MainViewModel(
    private val productDao: ProductDao = ProductDao(),
): ViewModel() {
    private val _products: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val products: LiveData<List<Product>> get() = _products


    init {
        _products.value = productDao.findAll()
    }
}
