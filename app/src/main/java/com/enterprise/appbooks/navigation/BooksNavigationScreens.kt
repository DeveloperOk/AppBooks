package com.enterprise.appbooks.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed class BooksNavigationScreens {

    // Define a splash screen route that doesn't take any arguments
    @Serializable
    object SplashScreenRoute : BooksNavigationScreens()

    // Define a main screen route that doesn't take any arguments
    @Serializable
    object MainScreenRoute : BooksNavigationScreens()

    // Define a BookListScreen route that doesn't take any arguments
    @Serializable
    object BookListScreenRoute : BooksNavigationScreens()

    @Serializable
    data class BookDetailScreenRoute(val appBookSerialized: String) : BooksNavigationScreens()

}