package com.enterprise.appbooks.screens.main

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.enterprise.appbooks.R
import com.enterprise.appbooks.navigation.BooksScreens
import com.enterprise.appbooks.ui.theme.AppPrimaryColor
import com.enterprise.appbooks.utils.internet.InternetManager
import com.enterprise.appbooks.viewmodel.MainViewModel


@Composable
fun MainScreen(navController: NavController, mainViewModel: MainViewModel){

    BodyContent(navController, mainViewModel)

}


@Composable
fun BodyContent(
    navController: NavController, mainViewModel: MainViewModel
) {



    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

        val (topBar, mainBody, progressIndicator) = createRefs()

        Surface(shadowElevation = 10.dp, modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .constrainAs(topBar) {
                top.linkTo(parent.top, margin = 20.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }){
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)) {
                Text(text = stringResource(R.string.main_screen_title),style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            }
        }




        val context = LocalContext.current
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .constrainAs(mainBody){
                    top.linkTo(parent.top, margin = 10.dp)
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                    start.linkTo(parent.start, margin = 10.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                }) {


            Image(
                painter = painterResource(id = R.drawable.baseline_menu_book_200),
                contentDescription = "book",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(200.dp)
            )

            Button(
                enabled = mainViewModel.isMainScreenButtonsEnabled.value,
                onClick = {

                    if(InternetManager.isInternetAvailable(context)){
                        mainViewModel.isMainScreenButtonsEnabled.value = false
                        mainViewModel.mainScreenShowProgressIndicator.value = true
                        mainViewModel.getBooks(context)
                    }else {
                        Toast.makeText(context, R.string.internet_is_not_available, Toast.LENGTH_LONG).show()
                    }

                     },
                colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryColor)
            ) {
                Text(text = stringResource(R.string.main_screen_download_books_button))
            }

            Button(
                enabled = mainViewModel.isMainScreenButtonsEnabled.value,
                onClick = { navController.navigate(BooksScreens.BookListScreen.name) },
                colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryColor)
            ) {
                Text(text = stringResource(R.string.main_screen_list_books_button))
            }

        }

        if(mainViewModel.mainScreenShowProgressIndicator.value){

            CircularProgressIndicator(modifier = Modifier
                .constrainAs(progressIndicator) {
                    top.linkTo(parent.top, margin = 10.dp)
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                    start.linkTo(parent.start, margin = 10.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                })

        }

    }

}



