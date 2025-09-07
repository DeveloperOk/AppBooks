package com.enterprise.appbooks.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.enterprise.appbooks.model.AppBook
import com.enterprise.appbooks.model.screens.BookDetailScreenData
import com.enterprise.appbooks.screens.bookdetail.BookDetailScreen
import com.enterprise.appbooks.screens.booklist.BookListScreen
import com.enterprise.appbooks.screens.main.MainScreen
import com.enterprise.appbooks.screens.splash.SplashScreen
import com.google.gson.Gson

@Composable
fun BooksNavigation(activityFinisher: () -> Unit) {

    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = BooksNavigationScreens.SplashScreenRoute
    ) {

        composable<BooksNavigationScreens.SplashScreenRoute>{
            SplashScreen(navController = navController)
        }

      composable<BooksNavigationScreens.MainScreenRoute>{
          MainScreen(navController = navController, activityFinisher = activityFinisher)
      }

      composable<BooksNavigationScreens.BookListScreenRoute>{
          BookListScreen(navController = navController)
      }

      composable<BooksNavigationScreens.BookDetailScreenRoute>{backStackEntry ->

            val bookDetailScreenRoute: BooksNavigationScreens.BookDetailScreenRoute = backStackEntry.toRoute()

            val appBook: AppBook = remember {
              val gson = Gson()
              val appBook = gson.fromJson(bookDetailScreenRoute.appBookSerialized, AppBook::class.java)
              appBook
            }

            val bookDetailScreenData: BookDetailScreenData = BookDetailScreenData(appBook = appBook)

            BookDetailScreen(
                navController = navController,
                bookDetailScreenData = bookDetailScreenData)
      }

    }

}