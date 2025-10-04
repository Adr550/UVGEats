package com.uvg.uvgeats.ui.navigation

import android.R.drawable
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uvg.uvgeats.ui.components.DetailScreen
import com.uvg.uvgeats.ui.components.FoodItem
import com.uvg.uvgeats.ui.components.LoginScreen
import com.uvg.uvgeats.ui.components.RegisterScreen
import com.uvg.uvgeats.ui.components.SearchScreen
import com.uvg.uvgeats.ui.components.WelcomeScreen

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = routes.welcome){
        composable(routes.welcome){
            WelcomeScreen (
                onCreateAccountClick={ navController.navigate(routes.register) },
                onLoginClick={ navController.navigate(routes.login) }
            )
        }
        composable(routes.login){
            LoginScreen(
                onLoginClick={ navController.navigate(routes.search) },
                onForgotPasswordClick={ navController.navigate(routes.register) }
            )
        }
        composable(routes.register){
            RegisterScreen(
                onRegisterClick={ navController.navigate(routes.search) }
            )
        }
        composable(routes.search){
            SearchScreen(
                listOf(
                    FoodItem("Hamburguesa", "Gitane", drawable.ic_menu_camera),
                    FoodItem("Crepa", "Saúl", drawable.ic_menu_gallery),
                    FoodItem("Camarones", "Gitane", drawable.ic_menu_report_image),
                    FoodItem("Lays", "Gitane", drawable.ic_menu_slideshow),
                    FoodItem("Pizza", "Gitane", drawable.ic_menu_gallery),
                    FoodItem("Tacos", "Gitane", drawable.ic_menu_camera),
                    FoodItem("Ensalada", "Gitane", drawable.ic_menu_report_image),
                    FoodItem("Sushi", "Gitane", drawable.ic_menu_slideshow),
                ),
                onItemClick={ navController.navigate(routes.detail) }
            )
        }
        composable(routes.detail){
            DetailScreen(
                onBackClick={ navController.navigate(routes.back) },
                food = FoodItem(
                    "Hamburguesa", "Gitane", drawable.ic_menu_camera
                )
            )
        }
        composable(routes.back){
            navController.popBackStack()
        }
    }
}