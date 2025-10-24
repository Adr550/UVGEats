package com.uvg.uvgeats.ui.navigation

import android.R.drawable
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.uvg.uvgeats.ui.components.DetailScreen
import com.uvg.uvgeats.ui.components.FoodItem
import com.uvg.uvgeats.ui.components.LoginScreen
import com.uvg.uvgeats.ui.components.RegisterScreen
import com.uvg.uvgeats.ui.components.SearchScreen
import com.uvg.uvgeats.ui.components.WelcomeScreen

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = NavigationRoutes.auth_graph) {

        // Grapho de autenticación
        navigation(startDestination = NavigationRoutes.welcome, route = NavigationRoutes.auth_graph) {
            composable(NavigationRoutes.welcome) {
                WelcomeScreen(
                    onCreateAccountClick = { navController.navigate(NavigationRoutes.register) },
                    onLoginClick = { navController.navigate(NavigationRoutes.login) }
                )
            }

            composable(NavigationRoutes.login) {
                LoginScreen(
                    onLoginClick = {
                        // Navegar al graph principal limpiando el stack
                        navController.navigate(NavigationRoutes.main_graph) {
                            popUpTo(NavigationRoutes.auth_graph) { inclusive = true }
                        }
                    },
                    onForgotPasswordClick = { navController.navigate(NavigationRoutes.register) }
                )
            }

            composable(NavigationRoutes.register) {
                RegisterScreen(
                    onRegisterClick = {
                        // Navega al graph principal, limpiando el stack
                        navController.navigate(NavigationRoutes.main_graph) {
                            popUpTo(NavigationRoutes.auth_graph) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Graph principal
        navigation(startDestination = NavigationRoutes.search, route = NavigationRoutes.main_graph) {
            composable(NavigationRoutes.search) {
                SearchScreen(
                    listOf(
                        FoodItem("Hamburguesa", "Gitane", android.R.drawable.ic_menu_camera, 30, "Cafetería CIT"),
                        FoodItem("Crepa", "Saúl", android.R.drawable.ic_menu_gallery, 25, "Cafetería CIT"),
                        FoodItem("Camarones", "Gitane", android.R.drawable.ic_menu_report_image, 45, "Cafetería CIT"),
                        FoodItem("Lays", "Gitane", android.R.drawable.ic_menu_slideshow, 15, "Máquina espendedora"),
                        FoodItem("Pizza", "Gitane", android.R.drawable.ic_menu_gallery, 35, "Cafetería CIT"),
                        FoodItem("Tacos", "Gitane", android.R.drawable.ic_menu_camera, 28, "Cafatería CIT"),
                        FoodItem("Ensalada", "Gitane", android.R.drawable.ic_menu_report_image, 22, "Cafetería CIT"),
                        FoodItem("Sushi", "Gitane", android.R.drawable.ic_menu_slideshow, 40, "Cafetería CIT"),
                    ),
                    onItemClick = { foodItem ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("selectedFood", foodItem)
                        navController.navigate(NavigationRoutes.detail)
                    }
                )
            }

            composable(NavigationRoutes.detail) {
                val foodItem = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<FoodItem>("selectedFood")
                    ?: FoodItem("Hamburguesa", "Gitane", android.R.drawable.ic_menu_camera)

                DetailScreen(
                    onBackClick = { navController.popBackStack() },
                    food = foodItem
                )
            }
        }
    }
}