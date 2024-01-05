package com.enterprise.appbooks

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.enterprise.appbooks.constants.nytimes.ImageConstants
import com.enterprise.appbooks.constants.nytimes.NytimesApiConstants
import com.enterprise.appbooks.interfaces.retrofitnytimes.NytimesApi
import com.enterprise.appbooks.localdatasource.database.BookDatabase
import com.enterprise.appbooks.model.AppBook
import com.enterprise.appbooks.model.BigImage
import com.enterprise.appbooks.model.BooksData
import com.enterprise.appbooks.model.FavoriteBookLabel
import com.enterprise.appbooks.model.SmallImage
import com.enterprise.appbooks.navigation.BooksNavigation
import com.enterprise.appbooks.ui.theme.AppBooksTheme
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        thread {

            getBooks()

        }

        setContent {
            AppBooksTheme {
                // A surface container using the 'background' color from the theme

                Surface(color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()) {
                    Column(verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        BooksNavigation()

                    }

                }


            }
        }
    }


    private fun getBooks() {

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(NytimesApiConstants.BaseUrl)
            .build()


        val nytimesApi: NytimesApi = retrofitBuilder.create(NytimesApi::class.java)

        val retrofitData = nytimesApi.getBooks("current", "hardcover-fiction", NytimesApiConstants.ApiKey)

        retrofitData.enqueue(object : Callback<BooksData?> {
            override fun onResponse(call: Call<BooksData?>, response: Response<BooksData?>) {

                if(response.isSuccessful){

                    val responseBody = response?.body()
                    val books = responseBody?.results?.books

                    if(books != null){

                        for (book in books){

                            if(book.primaryIsbn13 != null){

                                var appBook = AppBook()
                                var smallImage = SmallImage()
                                var bigImage = BigImage()
                                var favoriteBookLabel = FavoriteBookLabel()

                                appBook.primaryIsbn13       = book.primaryIsbn13!!
                                smallImage.primaryIsbn13    = book.primaryIsbn13!!
                                bigImage.primaryIsbn13      = book.primaryIsbn13!!
                                favoriteBookLabel.primaryIsbn13 = book.primaryIsbn13!!

                                appBook.bookImage        = book.bookImage
                                appBook.bookImageWidth   = book.bookImageWidth
                                appBook.bookImageHeight  = book.bookImageHeight
                                appBook.title            = book.title
                                appBook.author           = book.author
                                appBook.rank             = book.rank
                                appBook.description      = book.description
                                appBook.publisher        = book.publisher


                                thread{
                                    BookDatabase.getDatabase(application).getBookDao().addAppBook(appBook)

                                    val bitmapSmall: Bitmap = Picasso.get().load(appBook.bookImage).resize(ImageConstants.SmallImageWidth,0).get()
                                    smallImage.smallImage = bitmapSmall
                                    BookDatabase.getDatabase(application).getSmallImageDao().addSmallImage(smallImage)

                                    val bitmapBig: Bitmap = Picasso.get().load(appBook.bookImage).resize(ImageConstants.BigImageWidth,0).get()
                                    bigImage.bigImage = bitmapBig
                                    BookDatabase.getDatabase(application).getBigImageDao().addBigImage(bigImage)

                                    favoriteBookLabel.favorite = false
                                    BookDatabase.getDatabase(application).getFavoriteBookLabelDao().insertFavoriteBookLabel(favoriteBookLabel)

                                }

                            }
                        }

                        Thread.sleep(2000)
                        Toast.makeText(applicationContext, R.string.main_activity_books_downloaded_message, Toast.LENGTH_LONG).show()

                    }

                }else{

                    Log.d("MainActivity", "Failure: "+ response.message().toString())

                }

            }

            override fun onFailure(call: Call<BooksData?>, t: Throwable) {
                Log.d("MainActivity", "onFailure: "+ t.message)
            }
        })

    }



}
