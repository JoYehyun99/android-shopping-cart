package woowacourse.shopping.view.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentShoppingCartBinding
import woowacourse.shopping.view.MainActivity
import woowacourse.shopping.view.cart.adapter.ShoppingCartAdapter
import woowacourse.shopping.view.detail.ProductDetailFragment
import woowacourse.shopping.view.viewmodel.MainViewModel

class ShoppingCartFragment : Fragment(), ShoppingCartActionHandler, NavigationActionHandler {
    private var _binding: FragmentShoppingCartBinding? = null
    val binding: FragmentShoppingCartBinding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: ShoppingCartAdapter
    private var currentPage = MIN_PAGE_COUNT
    private var totalItemSize = DEFAULT_ITEM_SIZE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentShoppingCartBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = (requireActivity() as MainActivity).viewModel
        initView()
        observeData()
    }

    private fun initView() {
        mainViewModel.loadPagingCartItem(CART_ITEM_LOAD_PAGING_SIZE)
        binding.shoppingCartActionHandler = this
        binding.navigationActionHandler = this
        binding.currentPage = currentPage
        adapter =
            ShoppingCartAdapter(
                shoppingCartActionHandler = this,
                navigationActionHandler = this,
                loadLastItem = {
                    mainViewModel.loadPagingCartItem(CART_ITEM_LOAD_PAGING_SIZE)
                },
            )
        binding.rvShoppingCart.adapter = adapter
    }

    private fun observeData() {
        mainViewModel.shoppingCart.cartItems.observe(viewLifecycleOwner) { cartItems ->
            totalItemSize = cartItems?.size ?: DEFAULT_ITEM_SIZE
            view?.post {
                updateRecyclerView()
            }
        }
    }

    override fun onBackButtonClicked() {
        parentFragmentManager.popBackStack()
    }

    override fun onCartItemClicked(productId: Long) {
        val productFragment =
            ProductDetailFragment().apply {
                arguments = ProductDetailFragment.createBundle(productId)
            }
        changeFragment(productFragment)
    }

    override fun onRemoveCartItemButtonClicked(cartItemId: Long) {
        mainViewModel.deleteShoppingCartItem(cartItemId)
    }

    override fun onPreviousPageButtonClicked() {
        if (isExistPrevPage()) {
            binding.currentPage = --currentPage
            updateRecyclerView()
        }
    }

    override fun onNextPageButtonClicked() {
        if (isExistNextPage()) {
            binding.currentPage = ++currentPage
            updateRecyclerView()
        } else {
            showMaxItemMessage()
        }
    }

    private fun changeFragment(nextFragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, nextFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun updateRecyclerView() {
        val startIndex = (currentPage - MIN_PAGE_COUNT) * CART_ITEM_PAGE_SIZE
        val endIndex = minOf(currentPage * CART_ITEM_PAGE_SIZE, totalItemSize)
        val newData =
            mainViewModel.shoppingCart.cartItems.value?.subList(startIndex, endIndex) ?: emptyList()
        adapter.updateCartItems(hasLastItem(endIndex), newData)
        updateImageButtonColor()
    }

    private fun hasLastItem(endIndex: Int): Boolean {
        return endIndex >= (mainViewModel.shoppingCart.cartItems.value?.size ?: DEFAULT_ITEM_SIZE)
    }

    private fun isExistPrevPage(): Boolean {
        return currentPage > MIN_PAGE_COUNT
    }

    private fun isExistNextPage(): Boolean {
        return currentPage * CART_ITEM_PAGE_SIZE < totalItemSize
    }

    private fun updateImageButtonColor() {
        binding.onPrevButton = isExistPrevPage()
        binding.onNextButton = isExistNextPage()
    }

    private fun showMaxItemMessage() = Toast.makeText(this.context, R.string.max_paging_data_message, Toast.LENGTH_SHORT).show()

    companion object {
        private const val CART_ITEM_LOAD_PAGING_SIZE = 5
        const val CART_ITEM_PAGE_SIZE = 3
        private const val MIN_PAGE_COUNT = 1
        const val DEFAULT_ITEM_SIZE = 0
    }
}
