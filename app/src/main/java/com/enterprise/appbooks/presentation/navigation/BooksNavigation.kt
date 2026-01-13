package com.enterprise.appbooks.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.enterprise.appbooks.domain.model.AppBook
import com.enterprise.appbooks.domain.model.screens.BookDetailScreenData
import com.enterprise.appbooks.presentation.screens.bookdetail.BookDetailScreen
import com.enterprise.appbooks.presentation.screens.booklist.BookListScreen
import com.enterprise.appbooks.presentation.screens.main.MainScreen
import com.enterprise.appbooks.presentation.screens.splash.SplashScreen
import com.google.gson.Gson



@Composable
fun BooksNavigation(activityFinisher: () -> Unit){

    //With rememberNavBackStack, backstack survives the configuration changes such as screen rotation etc.
    val backStack = rememberNavBackStack(
        //NavigationStartScreen
        BooksNavigationScreens.SplashScreenRoute
    )

    NavDisplay(
        backStack = backStack,

        onBack = { backStack.removeLastOrNull() },

        //In order to clear viewmodel, on navigation back
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),

        entryProvider = entryProvider {

            entry<BooksNavigationScreens.SplashScreenRoute> {
                SplashScreen(navigateToMainScreen =
                    { backStack.add(BooksNavigationScreens.MainScreenRoute)}
                )
            }

            entry<BooksNavigationScreens.MainScreenRoute> {
                MainScreen(navigateToBookListScreen =
                    { backStack.add(BooksNavigationScreens.BookListScreenRoute)},
                    activityFinisher = activityFinisher)
            }

            entry<BooksNavigationScreens.BookListScreenRoute> {
                BookListScreen(navigateToBookDetailScreen = { appBook ->
                    val gson = Gson()
                    val appBookSerialized = gson.toJson(appBook)
                    backStack.add(BooksNavigationScreens.BookDetailScreenRoute(appBookSerialized = appBookSerialized))
                })
            }


            entry<BooksNavigationScreens.BookDetailScreenRoute> { bookDetailScreenRoute ->

                val appBook: AppBook = remember {
                    val gson = Gson()
                    val appBook = gson.fromJson(bookDetailScreenRoute.appBookSerialized, AppBook::class.java)
                    appBook
                }

                val bookDetailScreenData: BookDetailScreenData = BookDetailScreenData(appBook = appBook)

                BookDetailScreen(bookDetailScreenData = bookDetailScreenData)

            }

        }
    )
}