package com.enterprise.appbooks.screens.booklist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.enterprise.appbooks.R
import com.enterprise.appbooks.model.AppBook
import com.enterprise.appbooks.model.FavoriteBookLabel
import com.enterprise.appbooks.model.SmallImage
import com.enterprise.appbooks.navigation.BooksScreens
import com.enterprise.appbooks.ui.theme.ListBookScreenRowBorder
import com.enterprise.appbooks.viewmodel.MainSharedViewModel
import com.enterprise.appbooks.viewmodel.booklist.BookListScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BookListScreen(navController: NavController,
                   mainSharedViewModel: MainSharedViewModel,
                   bookListScreenViewModel:BookListScreenViewModel = hiltViewModel<BookListScreenViewModel>()){

    LaunchedEffect(key1 = true, block = {

        bookListScreenViewModel.getAllAppBooks()

    } )


    LazyRow{

        item {

            LazyColumn(modifier = Modifier.padding(2.dp),
                contentPadding = PaddingValues(2.dp)
            ) {

                if(bookListScreenViewModel.mutableStateAllAppBooks.isNullOrEmpty()){

                    item{
                        Text(text = stringResource(R.string.no_data_to_display))
                    }

                }else{

                    items(bookListScreenViewModel.mutableStateAllAppBooks) { appBook ->

                        LazyColumnRow(navController, appBook, bookListScreenViewModel, mainSharedViewModel)

                    }

                }

            }

        }

    }

}

@Composable
fun LazyColumnRow(navController: NavController, appBook: AppBook, bookListScreenViewModel:BookListScreenViewModel, mainSharedViewModel: MainSharedViewModel ){

    Surface(shadowElevation = 10.dp,
        modifier = Modifier
            .padding(4.dp)
            .width(350.dp)
            .height(240.dp)
            .clickable {
                mainSharedViewModel.selectedAppBook = appBook
                navController.navigate(BooksScreens.BookDetailScreen.name)
            },
        shape = RoundedCornerShape(15.dp),
        color = Color.White,
        border = BorderStroke(
            width = 2.dp, color = ListBookScreenRowBorder
        )
    ) {
        LazyColumnRowBody(navController, appBook, bookListScreenViewModel)
    }

}

@Composable
fun LazyColumnRowBody(navController: NavController, appBook: AppBook, bookListScreenViewModel:BookListScreenViewModel){

    val context = LocalContext.current

    val smallImage = remember { mutableStateOf(SmallImage()) }
    val favoriteBookLabel = remember { mutableStateOf(FavoriteBookLabel()) }

    LaunchedEffect(key1 = true, block = {
        bookListScreenViewModel.viewModelScope.launch(Dispatchers.IO) {

            val tempSmallImage = bookListScreenViewModel.getSmallImage(appBook.primaryIsbn13)
            val tempFavoriteBookLabel = bookListScreenViewModel.getFavoriteBookLabel(appBook.primaryIsbn13)
            bookListScreenViewModel.viewModelScope.launch(Dispatchers.Main) {

                if (tempSmallImage != null) {
                    smallImage.value = tempSmallImage
                }

                tempFavoriteBookLabel?.let{
                    favoriteBookLabel.value = it
                }

            }

        }
    })

    smallImage.value.smallImage?.asImageBitmap()?.let {

        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)) {

            val (bookImage, informationContainer, favoriteImage) = createRefs()

            Image(
                modifier = Modifier
                    .width(150.dp)
                    .height(200.dp)
                    .constrainAs(bookImage) {
                        top.linkTo(parent.top, margin = 0.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                        bottom.linkTo(parent.bottom, margin = 0.dp)
                    },
                bitmap = it,
                contentDescription = "image of book",
                contentScale = ContentScale.Fit
            )

            ConstraintLayout(modifier = Modifier
                .wrapContentSize()
                .constrainAs(informationContainer) {
                    top.linkTo(parent.top, margin = 0.dp)
                    start.linkTo(bookImage.end, margin = 0.dp)
                    bottom.linkTo(parent.bottom, margin = 0.dp)
                }) {

                val (title, author) = createRefs()

                LazyRow(modifier= Modifier
                    .width(180.dp)
                    .constrainAs(title) {
                        top.linkTo(parent.top, margin = 0.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                        bottom.linkTo(author.top, margin = 0.dp)
                    }){

                    item {
                        appBook.title?.let { it1 ->
                            Text(text = it1,
                                Modifier.padding(2.dp),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                }

                LazyRow(modifier= Modifier
                    .width(180.dp)
                    .constrainAs(author) {
                        top.linkTo(title.bottom, margin = 0.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                        bottom.linkTo(parent.bottom, margin = 0.dp)
                    }){
                    item {
                        appBook.author?.let { it1 ->
                            Text(text = it1,
                                Modifier.padding(2.dp),
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                }

            }


            var favouriteImagePainter: Painter? = null
            if(favoriteBookLabel.value.favorite){
                favouriteImagePainter = painterResource(R.drawable.ic_baseline_favorite_50)
            }else{
                favouriteImagePainter = painterResource(R.drawable.ic_baseline_favorite_border_50)
            }

            Image(
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .constrainAs(favoriteImage) {
                        top.linkTo(parent.top, margin = 10.dp)
                        end.linkTo(parent.end, margin = 10.dp)
                    },
                painter = favouriteImagePainter,
                contentDescription = "image of favorite",
                contentScale = ContentScale.Fit
            )


        }
    }
    }



