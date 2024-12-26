package com.enterprise.appbooks.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.enterprise.appbooks.screens.bookdetail.BookDetailScreen
import com.enterprise.appbooks.screens.booklist.BookListScreen
import com.enterprise.appbooks.screens.main.MainScreen
import com.enterprise.appbooks.screens.splash.SplashScreen
import com.enterprise.appbooks.viewmodel.MainSharedViewModel

@Composable
fun BooksNavigation(mainSharedViewModel: MainSharedViewModel, activityFinisher: () -> Unit) {

    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = BooksScreens.SplashScreen.name ) {

        composable(BooksScreens.SplashScreen.name){
            SplashScreen(navController = navController)
        }

        composable(BooksScreens.MainScreen.name){
            MainScreen(navController = navController, activityFinisher = activityFinisher)
        }

        composable(BooksScreens.BookListScreen.name){
            BookListScreen(navController = navController, mainSharedViewModel = mainSharedViewModel)
        }

        composable(BooksScreens.BookDetailScreen.name){
            BookDetailScreen(navController = navController, mainSharedViewModel = mainSharedViewModel)
        }

    }

}