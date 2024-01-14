package com.enterprise.appbooks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.enterprise.appbooks.navigation.BooksNavigation
import com.enterprise.appbooks.ui.theme.AppBooksTheme
import com.enterprise.appbooks.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.concurrent.thread

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val activityFinisher : () -> Unit = {
            this.finish()
        }

        setContent {
            AppBooksTheme {
                // A surface container using the 'background' color from the theme

                Surface(color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()) {
                    Column(verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        BooksNavigation(mainViewModel, activityFinisher)

                    }

                }


            }
        }
    }




}
