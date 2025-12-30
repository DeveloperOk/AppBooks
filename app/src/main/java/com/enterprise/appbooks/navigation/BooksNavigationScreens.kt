package com.enterprise.appbooks.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
sealed interface BooksNavigationScreens {

    // Define a splash screen route that doesn't take any arguments
    @Serializable
    object SplashScreenRoute : BooksNavigationScreens, NavKey

    // Define a main screen route that doesn't take any arguments
    @Serializable
    object MainScreenRoute : BooksNavigationScreens, NavKey

    // Define a BookListScreen route that doesn't take any arguments
    @Serializable
    object BookListScreenRoute : BooksNavigationScreens, NavKey

    @Serializable
    data class BookDetailScreenRoute(val appBookSerialized: String) : BooksNavigationScreens, NavKey

}