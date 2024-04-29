package com.example.tictactoe



sealed class NavigationItem(val route: String) {
    object History : NavigationItem("history")
    object TicTacToe : NavigationItem("ticTacToe")
}

