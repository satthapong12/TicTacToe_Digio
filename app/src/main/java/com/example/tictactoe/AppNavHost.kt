package com.example.tictactoe

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tictactoe.History.PlayHistoryScreen


@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = NavigationItem.TicTacToe.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.TicTacToe.route) {
            TicTacToeScreen(navController = navController )
        }
        composable(NavigationItem.History.route) {
            PlayHistoryScreen(navController = navController)
        }
        // เพิ่ม action สำหรับการกลับไปหน้า TicTacToe

    }
}

