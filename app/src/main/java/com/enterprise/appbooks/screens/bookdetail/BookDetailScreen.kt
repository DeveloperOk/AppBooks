package com.enterprise.appbooks.screens.bookdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.enterprise.appbooks.R
import com.enterprise.appbooks.model.BigImage
import com.enterprise.appbooks.model.FavoriteBookLabel
import com.enterprise.appbooks.model.SmallImage
import com.enterprise.appbooks.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BookDetailScreen(navController: NavController, mainViewModel: MainViewModel){

    LazyRow(modifier = Modifier
        .fillMaxSize()){

        item{

            LazyColumn(modifier = Modifier
                .fillParentMaxSize(), verticalArrangement = Arrangement.Center){

                item{

                    MainContent(mainViewModel = mainViewModel)

                }

            }

        }

    }

}

@Composable
fun MainContent(mainViewModel: MainViewModel){

    val bigImage = remember { mutableStateOf(BigImage()) }
    val favoriteBookLabel = remember { mutableStateOf(FavoriteBookLabel()) }


    LaunchedEffect(key1 = true, block = {

        mainViewModel.viewModelScope.launch(Dispatchers.IO) {

            val tempBigImage = mainViewModel.selectedAppBook?.primaryIsbn13?.let {
                mainViewModel.getBigImage(
                    it
                )
            }
            val tempFavoriteBookLabel = mainViewModel.selectedAppBook?.primaryIsbn13?.let {
                mainViewModel.getFavoriteBookLabel(
                    it
                )
            }
            mainViewModel.viewModelScope.launch(Dispatchers.Main) {

                if (tempBigImage != null) {
                    bigImage.value = tempBigImage
                }

                tempFavoriteBookLabel?.let{
                    favoriteBookLabel.value = it
                }

            }

        }



    } )

    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .padding(4.dp)) {

        val (topSpacer, bookImage, favoriteImage, title, author, rank, publisher, description) = createRefs()

        Spacer(modifier = Modifier
            .height(100.dp)
            .width(100.dp)
            .constrainAs(topSpacer) {
                top.linkTo(parent.top, margin = 0.dp)
                start.linkTo(parent.start, margin = 0.dp)
                end.linkTo(parent.end, margin = 0.dp)
                bottom.linkTo(bookImage.top, margin = 0.dp)
            })


        bigImage.value.bigImage?.asImageBitmap()?.let {
            Image(
                modifier = Modifier
                    .width(300.dp)
                    .height(400.dp)
                    .constrainAs(bookImage) {
                        top.linkTo(topSpacer.bottom, margin = 0.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                        end.linkTo(parent.end, margin = 0.dp)
                        bottom.linkTo(title.top, margin = 0.dp)
                    },
                bitmap = it,
                contentDescription = "image of book",
                contentScale = ContentScale.Fit
            )
        }


        var favouriteImagePainter: Painter? = null
        if (favoriteBookLabel.value.favorite) {
            favouriteImagePainter = painterResource(R.drawable.ic_baseline_favorite_100)
        } else {
            favouriteImagePainter = painterResource(R.drawable.ic_baseline_favorite_border_100)
        }

        Image(
            modifier = Modifier
                .clickable {

                    mainViewModel.viewModelScope.launch(Dispatchers.IO) {

                        var temporaryFavoriteBookLabel =
                            mainViewModel.selectedAppBook?.primaryIsbn13?.let {
                                FavoriteBookLabel(
                                    it,
                                    !favoriteBookLabel.value.favorite
                                )
                            }

                        temporaryFavoriteBookLabel?.let { mainViewModel.addFavoriteBookLabel(it) }

                        mainViewModel.viewModelScope.launch(Dispatchers.Main) {

                            if (temporaryFavoriteBookLabel != null) {
                                favoriteBookLabel.value = temporaryFavoriteBookLabel
                            }

                        }

                    }

                }
                .width(100.dp)
                .height(100.dp)
                .constrainAs(favoriteImage) {
                    top.linkTo(parent.top, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                },
            painter = favouriteImagePainter,
            contentDescription = "image of favorite",
            contentScale = ContentScale.Fit
        )


        Text(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(stringResource(id = R.string.book_detail_screen_app_book_title))

            }
            withStyle(
                style = SpanStyle()
            ) {
                append(mainViewModel.selectedAppBook?.title)
            }
        },
            modifier = Modifier
                .padding(2.dp)
                .constrainAs(title) {
                    top.linkTo(bookImage.bottom, margin = 0.dp)
                    start.linkTo(parent.start, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    bottom.linkTo(author.top, margin = 0.dp)
                })


        Text(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(stringResource(id = R.string.book_detail_screen_app_book_author))

            }
            withStyle(
                style = SpanStyle()
            ) {
                append(mainViewModel.selectedAppBook?.author)
            }
        },
            modifier = Modifier
                .padding(2.dp)
                .constrainAs(author) {
                    top.linkTo(title.bottom, margin = 0.dp)
                    start.linkTo(parent.start, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    bottom.linkTo(rank.top, margin = 0.dp)
                })


        Text(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(stringResource(id = R.string.book_detail_screen_app_book_rank))

            }
            withStyle(
                style = SpanStyle()
            ) {
                append(mainViewModel.selectedAppBook?.rank.toString())
            }
        },
            modifier = Modifier
                .padding(2.dp)
                .constrainAs(rank) {
                    top.linkTo(author.bottom, margin = 0.dp)
                    start.linkTo(parent.start, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    bottom.linkTo(publisher.top, margin = 0.dp)
                })


        Text(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(stringResource(id = R.string.book_detail_screen_app_book_publisher))

            }
            withStyle(
                style = SpanStyle()
            ) {
                append(mainViewModel.selectedAppBook?.publisher)
            }
        },
            modifier = Modifier
                .padding(2.dp)
                .constrainAs(publisher) {
                    top.linkTo(rank.bottom, margin = 0.dp)
                    start.linkTo(parent.start, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    bottom.linkTo(description.top, margin = 0.dp)
                })


        Text(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(stringResource(id = R.string.book_detail_screen_app_book_description))

            }
            withStyle(
                style = SpanStyle()
            ) {
                append(mainViewModel.selectedAppBook?.description)
            }
        },
            modifier = Modifier
                .padding(2.dp)
                .constrainAs(description) {
                    top.linkTo(publisher.bottom, margin = 0.dp)
                    start.linkTo(parent.start, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                })

    }

}
