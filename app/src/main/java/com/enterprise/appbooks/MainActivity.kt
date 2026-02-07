package com.enterprise.appbooks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.enterprise.appbooks.presentation.navigation.BooksNavigation
import com.enterprise.appbooks.presentation.ui.theme.AppBooksTheme
import com.enterprise.appbooks.presentation.ui.theme.AppPrimaryColor
import com.enterprise.appbooks.presentation.ui.theme.AppWhite
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val activityFinisher : () -> Unit = {
            this.finish()
        }

        setContent {
            AppBooksApp(activityFinisher = activityFinisher)
        }
    }

}

@Composable
fun AppBooksApp(activityFinisher: () -> Unit) {

    AppBooksTheme {

        Surface(color = AppPrimaryColor,
            modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.systemBarsPadding().fillMaxSize().background(AppWhite)) {

                BooksNavigation(activityFinisher)

            }

        }

    }

}

