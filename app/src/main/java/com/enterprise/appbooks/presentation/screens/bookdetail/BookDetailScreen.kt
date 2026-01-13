package com.enterprise.appbooks.presentation.screens.bookdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.appbooks.R
import com.enterprise.appbooks.domain.model.AppBook
import com.enterprise.appbooks.domain.model.BigImage
import com.enterprise.appbooks.domain.model.FavoriteBookLabel
import com.enterprise.appbooks.domain.model.screens.BookDetailScreenData
import com.enterprise.appbooks.presentation.viewmodel.bookdetail.BookDetailScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BookDetailScreen(
    bookDetailScreenViewModel: BookDetailScreenViewModel = hiltViewModel<BookDetailScreenViewModel>(),
    bookDetailScreenData: BookDetailScreenData
){

    val appBook = bookDetailScreenData.appBook

    LazyRow(modifier = Modifier
        .fillMaxSize().background(color = Color.White)){

        item{

            LazyColumn(modifier = Modifier
                .fillParentMaxSize(), verticalArrangement = Arrangement.Center){

                item{

                    MainContent(
                        bookDetailScreenViewModel = bookDetailScreenViewModel, appBook = appBook,
                    )

                }

            }

        }

    }

}

@Composable
fun MainContent(
    bookDetailScreenViewModel: BookDetailScreenViewModel,
    appBook: AppBook
){

    val bigImage = remember { mutableStateOf(BigImage()) }
    val favoriteBookLabel = remember { mutableStateOf(FavoriteBookLabel()) }


    LaunchedEffect(key1 = true, block = {

        bookDetailScreenViewModel.viewModelScope.launch(Dispatchers.IO) {

            val tempBigImage = appBook?.primaryIsbn13?.let {
                bookDetailScreenViewModel.getBigImage(
                    it
                )
            }
            val tempFavoriteBookLabel = appBook?.primaryIsbn13?.let {
                bookDetailScreenViewModel.getFavoriteBookLabel(
                    it
                )
            }
            bookDetailScreenViewModel.viewModelScope.launch(Dispatchers.Main) {

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

        BookImage(modifier = Modifier.constrainAs(bookImage) {
            top.linkTo(topSpacer.bottom, margin = 0.dp)
            start.linkTo(parent.start, margin = 0.dp)
            end.linkTo(parent.end, margin = 0.dp)
            bottom.linkTo(title.top, margin = 0.dp)
        },
            bigImage = bigImage)

        FavouriteImage(modifier = Modifier.constrainAs(favoriteImage) {
            top.linkTo(parent.top, margin = 0.dp)
            end.linkTo(parent.end, margin = 0.dp)
        },
        favoriteBookLabel, bookDetailScreenViewModel, appBook = appBook)

        AppText(modifier = Modifier.constrainAs(title) {
            top.linkTo(bookImage.bottom, margin = 0.dp)
            start.linkTo(parent.start, margin = 0.dp)
            end.linkTo(parent.end, margin = 0.dp)
            bottom.linkTo(author.top, margin = 0.dp)
        }, label = stringResource(id = R.string.book_detail_screen_app_book_title),
            text = appBook?.title)

        AppText(modifier = Modifier
            .constrainAs(author) {
                top.linkTo(title.bottom, margin = 0.dp)
                start.linkTo(parent.start, margin = 0.dp)
                end.linkTo(parent.end, margin = 0.dp)
                bottom.linkTo(rank.top, margin = 0.dp)
            },
            label = stringResource(id = R.string.book_detail_screen_app_book_author),
            text = appBook?.author)

        AppText(modifier = Modifier.constrainAs(rank) {
            top.linkTo(author.bottom, margin = 0.dp)
            start.linkTo(parent.start, margin = 0.dp)
            end.linkTo(parent.end, margin = 0.dp)
            bottom.linkTo(publisher.top, margin = 0.dp)
        },
            label = stringResource(id = R.string.book_detail_screen_app_book_rank),
            text = appBook?.rank.toString())

        AppText(modifier = Modifier.constrainAs(publisher) {
            top.linkTo(rank.bottom, margin = 0.dp)
            start.linkTo(parent.start, margin = 0.dp)
            end.linkTo(parent.end, margin = 0.dp)
            bottom.linkTo(description.top, margin = 0.dp)
        },
        label = stringResource(id = R.string.book_detail_screen_app_book_publisher),
        text = appBook?.publisher)

        AppText(modifier = Modifier.constrainAs(description) {
                top.linkTo(publisher.bottom, margin = 0.dp)
                start.linkTo(parent.start, margin = 0.dp)
                end.linkTo(parent.end, margin = 0.dp)
                bottom.linkTo(parent.bottom, margin = 20.dp)
            },
            label = stringResource(id = R.string.book_detail_screen_app_book_description),
            text = appBook?.description)

    }

}

@Composable
private fun AppText(modifier : Modifier,
    label: String,
    text: String?
) {
    Text(text = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold
            )
        ) {
            append(label)
        }
        withStyle(
            style = SpanStyle()
        ) {
            append(text)
        }
    },
    modifier = modifier
            .padding(2.dp),
    textAlign = TextAlign.Center)
}

@Composable
private fun FavouriteImage(
    modifier: Modifier,
    favoriteBookLabel: MutableState<FavoriteBookLabel>,
    bookDetailScreenViewModel: BookDetailScreenViewModel,
    appBook: AppBook
) {
    var favouriteImagePainter: Painter? = null
    if (favoriteBookLabel.value.favorite) {
        favouriteImagePainter = painterResource(R.drawable.ic_baseline_favorite_100)
    } else {
        favouriteImagePainter = painterResource(R.drawable.ic_baseline_favorite_border_100)
    }

    Image(
        modifier = modifier
            .clickable {

                bookDetailScreenViewModel.viewModelScope.launch(Dispatchers.IO) {

                    var temporaryFavoriteBookLabel =
                        appBook?.primaryIsbn13?.let {
                            FavoriteBookLabel(
                                it,
                                !favoriteBookLabel.value.favorite
                            )
                        }

                    temporaryFavoriteBookLabel?.let {
                        bookDetailScreenViewModel.addFavoriteBookLabel(
                            it
                        )
                    }

                    bookDetailScreenViewModel.viewModelScope.launch(Dispatchers.Main) {

                        if (temporaryFavoriteBookLabel != null) {
                            favoriteBookLabel.value = temporaryFavoriteBookLabel
                        }

                    }

                }

            }
            .width(100.dp)
            .height(100.dp),
        painter = favouriteImagePainter,
        contentDescription = "image of favorite",
        contentScale = ContentScale.Fit
    )
}

@Composable
fun BookImage(modifier: Modifier, bigImage: MutableState<BigImage>) {

    bigImage.value.bigImage?.asImageBitmap()?.let {
        Image(
            modifier = modifier
                .width(300.dp)
                .height(400.dp),
            bitmap = it,
            contentDescription = "image of book",
            contentScale = ContentScale.Fit
        )
    }

}
