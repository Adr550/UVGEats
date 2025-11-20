package com.uvg.uvgeats.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.uvg.uvgeats.data.model.FoodItem
import com.uvg.uvgeats.ui.components.FavoritesScreen
import com.uvg.uvgeats.ui.components.FavoritesScreenRoute
import com.uvg.uvgeats.ui.detail.DetailScreenRoute
import com.uvg.uvgeats.ui.login.LoginScreenRoute
import com.uvg.uvgeats.ui.register.RegisterScreenRoute
import com.uvg.uvgeats.ui.search.SearchScreenRoute
import com.uvg.uvgeats.ui.welcome.WelcomeScreenRoute

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = NavigationRoutes.auth_graph) {

        // Graph de autenticación
        navigation(
            startDestination = NavigationRoutes.welcome,
            route = NavigationRoutes.auth_graph
        ) {
            composable(NavigationRoutes.welcome) {
                WelcomeScreenRoute(
                    onNavigateToLogin = {
                        navController.navigate(NavigationRoutes.login)
                    },
                    onNavigateToRegister = {
                        navController.navigate(NavigationRoutes.register)
                    }
                )
            }

            composable(NavigationRoutes.login) {
                LoginScreenRoute(
                    onNavigateToHome = {
                        navController.navigate(NavigationRoutes.main_graph) {
                            popUpTo(NavigationRoutes.auth_graph) { inclusive = true }
                        }
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(NavigationRoutes.register)
                    }
                )
            }

            composable(NavigationRoutes.register) {
                RegisterScreenRoute(
                    onNavigateToHome = {
                        navController.navigate(NavigationRoutes.main_graph) {
                            popUpTo(NavigationRoutes.auth_graph) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Graph principal
        navigation(
            startDestination = NavigationRoutes.search,
            route = NavigationRoutes.main_graph
        ) {
            composable(NavigationRoutes.search) {
                SearchScreenRoute(
                    onNavigateToDetail = { foodItem ->
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "selectedFood",
                            foodItem
                        )
                        navController.navigate(NavigationRoutes.detail)
                    },
                    onNavigateToFavorites = {
                        navController.navigate(NavigationRoutes.favorites)
                    }
                )
            }

            composable(NavigationRoutes.detail) {
                val foodItem = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<FoodItem>("selectedFood")
                    ?: FoodItem("Hamburguesa", "Gitane", android.R.drawable.ic_menu_camera)

                DetailScreenRoute(
                    foodItem = foodItem,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // ⭐ AQUÍ SE HIZO EL ÚNICO CAMBIO
            composable(NavigationRoutes.favorites) {
                FavoritesScreenRoute(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
