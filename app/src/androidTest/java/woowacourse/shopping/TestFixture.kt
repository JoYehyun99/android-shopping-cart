package woowacourse.shopping

import woowacourse.shopping.data.db.CartItemDatabase

object TestFixture {
    fun CartItemDatabase.deleteAll() {
        this.openHelper.writableDatabase.execSQL("DELETE FROM ${CartItemDatabase.CART_ITEMS_DB_NAME}")
    }
}
