package com.enterprise.appbooks.presentation.screens.booklist

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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.enterprise.appbooks.R
import com.enterprise.appbooks.domain.model.AppBook
import com.enterprise.appbooks.domain.model.FavoriteBookLabel
import com.enterprise.appbooks.domain.model.SmallImage
import com.enterprise.appbooks.presentation.ui.theme.ListBookScreenRowBorder
import com.enterprise.appbooks.presentation.viewmodel.booklist.BookListScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BookListScreen(navigateToBookDetailScreen: (AppBook) -> Unit,
                   bookListScreenViewModel:BookListScreenViewModel = hiltViewModel<BookListScreenViewModel>()){

    LaunchedEffect(key1 = true, block = {

        bookListScreenViewModel.getAllAppBooks()

    } )

    val allAppBooks = bookListScreenViewModel.mutableStateAllAppBooks.collectAsStateWithLifecycle()

    LazyRow{

        item {

            LazyColumn(modifier = Modifier.padding(2.dp),
                contentPadding = PaddingValues(2.dp)
            ) {

                if(allAppBooks.value.isNullOrEmpty()){

                    item{
                        Text(text = stringResource(R.string.no_data_to_display))
                    }

                }else{

                    items(allAppBooks.value) { appBook ->

                        LazyColumnRow(navigateToBookDetailScreen = navigateToBookDetailScreen, appBook, bookListScreenViewModel)

                    }

                }

            }

        }

    }

}

@Composable
fun LazyColumnRow(navigateToBookDetailScreen: (AppBook) -> Unit, appBook: AppBook, bookListScreenViewModel:BookListScreenViewModel){

    Surface(shadowElevation = 10.dp,
        modifier = Modifier
            .padding(4.dp)
            .width(350.dp)
            .height(240.dp)
            .clickable {
                navigateToBookDetailScreen(appBook)
            },
        shape = RoundedCornerShape(15.dp),
        color = Color.White,
        border = BorderStroke(
            width = 2.dp, color = ListBookScreenRowBorder
        )
    ) {
        LazyColumnRowBody(appBook, bookListScreenViewModel)
    }

}

@Composable
fun LazyColumnRowBody(appBook: AppBook, bookListScreenViewModel:BookListScreenViewModel){

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

            val (bookImage, titleAndAuthorContainer, favoriteImage) = createRefs()

            BookImage(modifier = Modifier.constrainAs(bookImage) {
                top.linkTo(parent.top, margin = 0.dp)
                start.linkTo(parent.start, margin = 0.dp)
                bottom.linkTo(parent.bottom, margin = 0.dp)
            }, imageBitmap = it)


            TitleAndAuthorContainer(modifier = Modifier.constrainAs(titleAndAuthorContainer) {
                top.linkTo(parent.top, margin = 0.dp)
                start.linkTo(bookImage.end, margin = 0.dp)
                bottom.linkTo(parent.bottom, margin = 0.dp)
            }, appBook = appBook)

            FavouriteImage(modifier = Modifier
                .constrainAs(favoriteImage) {
                    top.linkTo(parent.top, margin = 10.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                }, favoriteBookLabel = favoriteBookLabel)


        }
    }
    }

@Composable
fun TitleAndAuthorContainer(modifier: Modifier, appBook: AppBook) {

    ConstraintLayout(modifier = modifier
        .wrapContentSize()
    ) {

        val (title, author) = createRefs()

        TitleText(modifier = Modifier.constrainAs(title) {
            top.linkTo(parent.top, margin = 0.dp)
            start.linkTo(parent.start, margin = 0.dp)
            bottom.linkTo(author.top, margin = 0.dp)
        }, appBook = appBook)

        AuthorText(modifier= Modifier
            .constrainAs(author) {
                top.linkTo(title.bottom, margin = 0.dp)
                start.linkTo(parent.start, margin = 0.dp)
                bottom.linkTo(parent.bottom, margin = 0.dp)
            }, appBook = appBook)

    }

}

@Composable
private fun BookImage(
    modifier: Modifier,
    imageBitmap: ImageBitmap
) {
    Image(
        modifier = modifier
            .width(150.dp)
            .height(200.dp),
        bitmap = imageBitmap,
        contentDescription = "image of book",
        contentScale = ContentScale.Fit
    )
}

@Composable
fun FavouriteImage(modifier: Modifier, favoriteBookLabel: MutableState<FavoriteBookLabel>) {

    var favouriteImagePainter: Painter? = null
    if(favoriteBookLabel.value.favorite){
        favouriteImagePainter = painterResource(R.drawable.ic_baseline_favorite_50)
    }else{
        favouriteImagePainter = painterResource(R.drawable.ic_baseline_favorite_border_50)
    }

    Image(
        modifier = modifier
            .width(50.dp)
            .height(50.dp),
        painter = favouriteImagePainter,
        contentDescription = "image of favorite",
        contentScale = ContentScale.Fit
    )

}

@Composable
fun AuthorText(modifier: Modifier, appBook: AppBook) {
    LazyRow(modifier= modifier
        .width(180.dp)){
        item {
            appBook.author?.let { it1 ->
                Text(text = it1,
                    Modifier.padding(2.dp),
                    style = MaterialTheme.typography.bodyMedium)
            }
        }

    }
}

@Composable
private fun TitleText(
    modifier: Modifier,
    appBook: AppBook
) {
    LazyRow(modifier = modifier
        .width(180.dp)
        ) {

        item {
            appBook.title?.let { it1 ->
                Text(
                    text = it1,
                    Modifier.padding(2.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

    }
}



