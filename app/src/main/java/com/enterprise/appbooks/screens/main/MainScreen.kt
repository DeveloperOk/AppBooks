package com.enterprise.appbooks.screens.main

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.enterprise.appbooks.R
import com.enterprise.appbooks.navigation.BooksScreens
import com.enterprise.appbooks.ui.theme.AppPrimaryColor
import com.enterprise.appbooks.ui.theme.ProgressBarEndColor
import com.enterprise.appbooks.ui.theme.ProgressBarStartColor
import com.enterprise.appbooks.utils.internet.InternetManager
import com.enterprise.appbooks.viewmodel.main.MainScreenViewModel


@Composable
fun MainScreen(navController: NavController, activityFinisher : () -> Unit,
               mainScreenViewModel: MainScreenViewModel = hiltViewModel<MainScreenViewModel>()
){

    BackHandler{
        activityFinisher()
    }

    BodyContent(navController, mainScreenViewModel)

}


@Composable
fun BodyContent(
    navController: NavController,
    mainScreenViewModel: MainScreenViewModel
) {



    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

        val (topBar, mainBody, progressIndicator, progressPercent) = createRefs()

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
                enabled = mainScreenViewModel.isMainScreenButtonsEnabled.value,
                onClick = {

                    if(InternetManager.isInternetAvailable(context)){
                        mainScreenViewModel.mainScreenProgressBarFactor.value = 0.0f
                        mainScreenViewModel.mainScreenProgressBarPercent.value = 0
                        mainScreenViewModel.isMainScreenButtonsEnabled.value = false
                        mainScreenViewModel.mainScreenShowProgressIndicator.value = true

                        mainScreenViewModel.getBooks(context)
                    }else {
                        Toast.makeText(context, R.string.internet_is_not_available, Toast.LENGTH_LONG).show()
                    }

                     },
                colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryColor)
            ) {
                Text(text = stringResource(R.string.main_screen_download_books_button))
            }

            Button(
                enabled = mainScreenViewModel.isMainScreenButtonsEnabled.value,
                onClick = { navController.navigate(BooksScreens.BookListScreen.name) },
                colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryColor)
            ) {
                Text(text = stringResource(R.string.main_screen_list_books_button))
            }

        }

        if(mainScreenViewModel.mainScreenShowProgressIndicator.value){

                Row(
                    modifier = Modifier
                    .constrainAs(progressIndicator) {
                        top.linkTo(parent.top, margin = 10.dp)
                        bottom.linkTo(parent.bottom, margin = 10.dp)
                        start.linkTo(parent.start, margin = 10.dp)
                        end.linkTo(parent.end, margin = 10.dp)
                    }
                        .padding(3.dp)
                        .fillMaxWidth(0.8f)
                        .height(35.dp)
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Red,
                                    Color.Red
                                )
                            ),
                            shape = RoundedCornerShape(34.dp)
                        )
                        .clip(
                            RoundedCornerShape(
                                topStartPercent = 50,
                                topEndPercent = 50,
                                bottomEndPercent = 50,
                                bottomStartPercent = 50
                            )
                        )
                        .background(Color.White),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val isButtonVisible = !mainScreenViewModel.mainScreenProgressBarFactor.value.equals(0.0f)
                    if(isButtonVisible) {
                        Button(
                            contentPadding = PaddingValues(1.dp),
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth(mainScreenViewModel.mainScreenProgressBarFactor.value)
                                .background(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            ProgressBarStartColor, ProgressBarEndColor
                                        )
                                    )
                                ),
                            enabled = false,
                            elevation = null,
                        ) { }
                    }

                }

                Text(text = mainScreenViewModel.mainScreenProgressBarPercent.value.toString()+ stringResource(
                    id = R.string.main_screen_percent_sign), color = Color.Black,
                    modifier = Modifier.constrainAs(progressPercent) {
                    top.linkTo(parent.top, margin = 10.dp)
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                    start.linkTo(parent.start, margin = 10.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                })


            }



    }

}



