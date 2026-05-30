package com.refresh.pos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.refresh.pos.ui.screen.MainScreen
import com.refresh.pos.ui.screen.ProductDetailScreen
import com.refresh.pos.ui.screen.SaleDetailScreen
import com.refresh.pos.ui.screen.SplashScreen

@Composable
fun PosNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToSaleDetail = { saleId ->
                    navController.navigate(Screen.SaleDetail.createRoute(saleId))
                }
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) {
            ProductDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SaleDetail.route,
            arguments = listOf(navArgument("saleId") { type = NavType.LongType })
        ) { backStackEntry ->
            val saleId = backStackEntry.arguments?.getLong("saleId") ?: 0L
            SaleDetailScreen(
                saleId = saleId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
