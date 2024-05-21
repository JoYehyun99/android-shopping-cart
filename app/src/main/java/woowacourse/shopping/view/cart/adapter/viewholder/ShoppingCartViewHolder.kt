package woowacourse.shopping.view.cart.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemShoppingCartBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.view.cart.ShoppingCartActionHandler

class ShoppingCartViewHolder(
    private val binding: ItemShoppingCartBinding,
    private val shoppingCartActionHandler: ShoppingCartActionHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(cartItem: CartItem) {
        binding.cartItem = cartItem
        binding.shoppingCartActionHandler = shoppingCartActionHandler
    }
}
