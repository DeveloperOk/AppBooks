package com.enterprise.appbooks.presentation.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enterprise.appbooks.R
import com.enterprise.appbooks.presentation.ui.theme.AppPrimaryColor
import com.enterprise.appbooks.presentation.ui.theme.ProgressBarEndColor
import com.enterprise.appbooks.presentation.ui.theme.ProgressBarStartColor
import com.enterprise.appbooks.presentation.viewmodel.main.MainScreenViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.Unit


@Composable
fun MainScreen(navigateToBookListScreen: () -> Unit, activityFinisher : () -> Unit,
               mainScreenViewModel: MainScreenViewModel = hiltViewModel<MainScreenViewModel>()
){

    BackHandler{
        activityFinisher()
    }

    BodyContent(navigateToBookListScreen = navigateToBookListScreen, mainScreenViewModel)

}


@Composable
fun BodyContent(
    navigateToBookListScreen: () -> Unit,
    mainScreenViewModel: MainScreenViewModel
) {


    val isNoInternetConnectionDialogVisible = mainScreenViewModel.isNoInternetConnectionDialogVisible.collectAsStateWithLifecycle()

    if(isNoInternetConnectionDialogVisible.value){

        NoInternetConnectionDialog(isDialogVisible = mainScreenViewModel.isNoInternetConnectionDialogVisible)

    }

    val mainScreenShowProgressIndicator = mainScreenViewModel.mainScreenShowProgressIndicator.collectAsStateWithLifecycle()

    if(mainScreenShowProgressIndicator.value){

        AppProgressIndicator(mainScreenViewModel.mainScreenProgressBarFactor,
            mainScreenViewModel.mainScreenProgressBarPercent)

    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

        val (topBar, mainBody) = createRefs()


        TopBar(modifier = Modifier.constrainAs(topBar) {
            top.linkTo(parent.top, margin = 20.dp)
            start.linkTo(parent.start, margin = 20.dp)
            end.linkTo(parent.end, margin = 20.dp)
        })

        MainBody(modifier = Modifier.constrainAs(mainBody){
            top.linkTo(parent.top, margin = 10.dp)
            bottom.linkTo(parent.bottom, margin = 10.dp)
            start.linkTo(parent.start, margin = 10.dp)
            end.linkTo(parent.end, margin = 10.dp)
        }, mainScreenViewModel = mainScreenViewModel, navigateToBookListScreen = navigateToBookListScreen)

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppProgressIndicator(inputProgressBarFactor: MutableStateFlow<Float>, inputProgressBarPercent: MutableStateFlow<Int>) {

    val progressBarFactor = inputProgressBarFactor.collectAsStateWithLifecycle()
    val progressBarPercent = inputProgressBarPercent.collectAsStateWithLifecycle()

    BasicAlertDialog(onDismissRequest = {
        // Dismiss the dialog when the user clicks outside the dialog or on the back button.
        //isDialogVisible.value = false
    }) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {

            val (progressIndicator, progressPercent) = createRefs()

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

                val isButtonVisible = !progressBarFactor.value.equals(0.0f)
                if (isButtonVisible) {
                    Button(
                        contentPadding = PaddingValues(1.dp),
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth(progressBarFactor.value)
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

            Text(text = progressBarPercent.value.toString() + stringResource(
                id = R.string.main_screen_percent_sign
            ), color = Color.Black,
                modifier = Modifier.constrainAs(progressPercent) {
                    top.linkTo(parent.top, margin = 10.dp)
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                    start.linkTo(parent.start, margin = 10.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                })

        }
    }
}

@Composable
fun MainBody(modifier: Modifier, mainScreenViewModel:MainScreenViewModel, navigateToBookListScreen: () -> Unit,) {
    val context = LocalContext.current

    val isMainScreenButtonsEnabled = mainScreenViewModel.isMainScreenButtonsEnabled.collectAsStateWithLifecycle()

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {

        Image(
            painter = painterResource(id = R.drawable.baseline_menu_book_200),
            contentDescription = "book",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(200.dp)
        )

        Button(
            enabled = isMainScreenButtonsEnabled.value,
            onClick = {

                mainScreenViewModel.getBooks(context)

            },
            colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryColor)
        ) {
            Text(text = stringResource(R.string.main_screen_download_books_button))
        }

        Button(
            enabled = isMainScreenButtonsEnabled.value,
            onClick = { navigateToBookListScreen() },
            colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryColor)
        ) {
            Text(text = stringResource(R.string.main_screen_list_books_button))
        }

    }

}

@Composable
fun TopBar(modifier: Modifier) {
    Surface(shadowElevation = 10.dp, modifier = modifier
        .padding(10.dp)
        .fillMaxWidth()
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)) {
            Text(text = stringResource(R.string.main_screen_title),style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoInternetConnectionDialog(isDialogVisible: MutableStateFlow<Boolean>) {

    BasicAlertDialog(onDismissRequest = {
        // Dismiss the dialog when the user clicks outside the dialog or on the back button.
        //isDialogVisible.value = false
    }){

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.wrapContentHeight()
                .wrapContentWidth()
                .background(color = Color.White, RoundedCornerShape(size = 15.dp))
                .border(width = 3.dp, color = Color.Green, RoundedCornerShape(size = 15.dp))
                .padding(15.dp)){

            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().wrapContentHeight()){

                Icon(imageVector = Icons.Default.Info,
                    contentDescription
                    = stringResource(id = R.string.no_internet_connection_popup_icon_content_description),
                    tint = Color.Blue)

                Spacer(modifier = Modifier.width(5.dp))

                Text(text = stringResource(id = R.string.no_internet_connection_popup_title))


            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(text = stringResource(id = R.string.no_internet_connection_popup_message))

            Spacer(modifier = Modifier.height(15.dp))

            Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                onClick = {

                    isDialogVisible.update{ currentValue -> false }

                }) {

                Text(text = stringResource(id = R.string.no_internet_connection_neutral_button_text))

            }

        }

    }

}



