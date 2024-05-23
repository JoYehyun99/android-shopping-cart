package woowacourse.shopping.view.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

class ProductListViewModelFactory(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
            ProductListViewModel(productRepository, cartRepository) as T
        } else {
            throw IllegalArgumentException(UNKNOWN_VIEWMODEL)
        }
    }

    companion object {
        private const val UNKNOWN_VIEWMODEL = "Unknown ViewModel class"
    }
}
