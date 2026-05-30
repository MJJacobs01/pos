package com.refresh.pos.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Main : Screen("main")
    data object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Long) = "product_detail/$productId"
    }
    data object SaleDetail : Screen("sale_detail/{saleId}") {
        fun createRoute(saleId: Long) = "sale_detail/$saleId"
    }
}
