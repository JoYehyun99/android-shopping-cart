package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product

interface CartRepository {
    fun addCartItem(product: Product): Long

    fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): List<CartItem>

    fun deleteCartItem(itemId: Long)

    fun hasNextCartItemPage(
        currentPage: Int,
        itemsPerPage: Int,
    ): Boolean
}
