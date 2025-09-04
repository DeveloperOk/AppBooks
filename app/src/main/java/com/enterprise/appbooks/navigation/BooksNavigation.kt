package com.enterprise.appbooks.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.enterprise.appbooks.model.screens.BookDetailScreenData
import com.enterprise.appbooks.model.screens.BookListScreenData
import com.enterprise.appbooks.model.screens.MainScreenData
import com.enterprise.appbooks.model.screens.SplashScreenData
import com.enterprise.appbooks.screens.bookdetail.BookDetailScreen
import com.enterprise.appbooks.screens.booklist.BookListScreen
import com.enterprise.appbooks.screens.main.MainScreen
import com.enterprise.appbooks.screens.splash.SplashScreen

@Composable
fun BooksNavigation(activityFinisher: () -> Unit) {

    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = SplashScreenData
    ) {

        composable<SplashScreenData>{
            SplashScreen(navController = navController)
        }

      composable<MainScreenData>{
          MainScreen(navController = navController, activityFinisher = activityFinisher)
      }

      composable<BookListScreenData>{
          BookListScreen(navController = navController)
      }

      composable<BookDetailScreenData>{backStackEntry ->
            val bookDetailScreenData: BookDetailScreenData = backStackEntry.toRoute()
            BookDetailScreen(
                navController = navController,
                bookDetailScreenData = bookDetailScreenData)
      }

    }

}