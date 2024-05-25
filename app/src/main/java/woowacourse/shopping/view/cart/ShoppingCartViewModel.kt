package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.utils.Event
import woowacourse.shopping.view.CountActionHandler
import woowacourse.shopping.view.ProductUpdate
import kotlin.concurrent.thread

class ShoppingCartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), ShoppingCartActionHandler, CountActionHandler {
    private val _cartItems: MutableLiveData<List<CartItem>> = MutableLiveData(listOf())
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    private val _currentPage: MutableLiveData<Int> = MutableLiveData(1)
    val currentPage: LiveData<Int> get() = _currentPage

    private val _isPrevButtonActivated: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPrevButtonActivated: LiveData<Boolean> get() = _isPrevButtonActivated

    private val _isNextButtonActivated: MutableLiveData<Boolean> = MutableLiveData(false)
    val isNextButtonActivated: LiveData<Boolean> get() = _isNextButtonActivated

    private val _pagedData: MutableLiveData<List<CartItem>> = MutableLiveData(emptyList())
    val pagedData: LiveData<List<CartItem>> get() = _pagedData

    private val _navigateToBack = MutableLiveData<Event<Boolean>>()
    val navigateToBack: LiveData<Event<Boolean>> get() = _navigateToBack

    private val _navigateToDetail = MutableLiveData<Event<Long>>()
    val navigateToDetail: LiveData<Event<Long>> get() = _navigateToDetail

    private val _updatedCountInfo: MutableLiveData<ProductUpdate> = MutableLiveData()
    val updatedCountInfo: LiveData<ProductUpdate> get() = _updatedCountInfo

    init {
        loadCartItems()
        updatePagedData()
    }

    private fun loadCartItems() {
        var pagingData = emptyList<CartItem>()
        thread {
            val itemSize = getItemCount()
            pagingData = cartRepository.loadPagingCartItems(itemSize, CART_ITEM_LOAD_PAGING_SIZE)
        }.join()

        if (pagingData.isNotEmpty()) {
            addProducts(pagingData)
            updateButtonState()
        }
    }

    override fun onRemoveCartItemButtonClicked(cartItem: CartItem) {
        cartRepository.deleteCartItem(cartItem.id)
        deleteProduct(cartItem.id)
        _updatedCountInfo.value = ProductUpdate(cartItem.product.id, REMOVED_VALUE)
        updatePagedData()
    }

    override fun onPreviousPageButtonClicked() {
        _currentPage.value = _currentPage.value?.minus(1)
        updatePagedData()
    }

    override fun onNextPageButtonClicked() {
        _currentPage.value = _currentPage.value?.plus(1)
        updatePagedData()
    }

    override fun onBackButtonClicked() {
        _navigateToBack.value = Event(true)
    }

    override fun onCartItemClicked(productId: Long) {
        _navigateToDetail.value = Event(productId)
    }

    private fun updatePagedData() {
        val pageNumber = currentPage.value ?: 1
        val startIndex = (pageNumber - MIN_PAGE_COUNT) * CART_ITEM_PAGE_SIZE
        if (shouldLoadCartItems(pageNumber)) {
            loadCartItems()
        }
        val endIndex = minOf(pageNumber * CART_ITEM_PAGE_SIZE, getItemCount())
        val newData = cartItems.value?.subList(startIndex, endIndex) ?: emptyList()
        _pagedData.value = newData

        updateButtonState()
    }

    private fun shouldLoadCartItems(pageNumber: Int): Boolean {
        return pageNumber * CART_ITEM_PAGE_SIZE > getItemCount()
    }

    private fun updateButtonState() {
        val pageNumber = currentPage.value ?: 1
        _isPrevButtonActivated.value = pageNumber > MIN_PAGE_COUNT
        _isNextButtonActivated.value = cartRepository.hasNextCartItemPage(pageNumber, CART_ITEM_PAGE_SIZE)
    }

    private fun addProducts(cartItems: List<CartItem>) {
        _cartItems.value = _cartItems.value?.plus(cartItems)
    }

    private fun deleteProduct(itemId: Long) {
        _cartItems.value = _cartItems.value?.filter { it.id != itemId }
    }

    private fun getItemCount(): Int {
        return _cartItems.value?.size ?: 0
    }

    override fun onIncreaseQuantityButtonClicked(product: Product) {
        val updatedQuantity = cartRepository.updateIncrementQuantity(product, INCREMENT_VALUE)
        _updatedCountInfo.value = ProductUpdate(product.id, updatedQuantity)
    }

    override fun onDecreaseQuantityButtonClicked(product: Product) {
        val updatedQuantity = cartRepository.updateDecrementQuantity(product, DECREMENT_VALUE, false)
        if (updatedQuantity > 1) {
            _updatedCountInfo.value = ProductUpdate(product.id, updatedQuantity)
        }
    }

    companion object {
        private const val CART_ITEM_LOAD_PAGING_SIZE = 5
        const val CART_ITEM_PAGE_SIZE = 3
        private const val MIN_PAGE_COUNT = 1
        const val INCREMENT_VALUE = 1
        const val DECREMENT_VALUE = 1
        const val REMOVED_VALUE = 0
    }
}
