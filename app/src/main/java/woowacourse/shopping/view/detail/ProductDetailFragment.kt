package woowacourse.shopping.view.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import woowacourse.shopping.databinding.FragmentProductDetailBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.utils.NoSuchDataException
import woowacourse.shopping.view.MainActivity
import woowacourse.shopping.view.viewmodel.MainViewModel

class ProductDetailFragment : Fragment(), OnClickDetail {
    private var _binding: FragmentProductDetailBinding? = null
    val binding: FragmentProductDetailBinding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = (requireActivity() as MainActivity).viewModel
        val product = loadProduct()
        if (product != null) {
            initView(product)
        }
    }

    private fun receiveId(): Long {
        return arguments?.getLong(PRODUCT_ID) ?: throw NoSuchDataException()
    }

    private fun loadProduct(): Product? {
        runCatching {
            mainViewModel.loadProductItem(receiveId())
        }.onSuccess {
            return it
        }.onFailure {
            showLoadErrorMessage()
            parentFragmentManager.popBackStack()
        }
        return null
    }

    private fun initView(product: Product) {
        binding.product = product
        binding.onClickDetail = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun clickClose() {
        parentFragmentManager.popBackStack()
    }

    override fun clickAddCart(product: Product) {
        runCatching {
            mainViewModel.addShoppingCartItem(product)
        }.onSuccess {
            showAddCartSuccessMessage()
        }.onFailure {
            showAddCartErrorMessage()
        }
    }

    private fun showLoadErrorMessage() = Toast.makeText(this.context, ERROR_DATA_LOAD_MESSAGE, Toast.LENGTH_SHORT).show()

    private fun showAddCartSuccessMessage() = Toast.makeText(this.context, SUCCESS_SAVE_DATA, Toast.LENGTH_SHORT).show()

    private fun showAddCartErrorMessage() = Toast.makeText(this.context, ERROR_SAVE_DATA, Toast.LENGTH_SHORT).show()

    companion object {
        fun createBundle(id: Long): Bundle {
            return Bundle().apply { putLong(PRODUCT_ID, id) }
        }

        private const val ERROR_SAVE_DATA = "장바구니에 담기지 않았습니다.."
        private const val SUCCESS_SAVE_DATA = "장바구니에 담겼습니다!"
        private const val PRODUCT_ID = "productId"
        private const val ERROR_DATA_LOAD_MESSAGE = "데이터가 없습니다!"
    }
}
