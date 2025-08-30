package com.enterprise.appbooks.viewmodel.main

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.appbooks.R
import com.enterprise.appbooks.constants.nytimes.ImageConstants
import com.enterprise.appbooks.exception.NoInternetConnectionException
import com.enterprise.appbooks.model.AppBook
import com.enterprise.appbooks.model.BigImage
import com.enterprise.appbooks.model.Book
import com.enterprise.appbooks.model.FavoriteBookLabel
import com.enterprise.appbooks.model.SmallImage
import com.enterprise.appbooks.repository.AppRepository
import com.squareup.picasso.Picasso
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject


@HiltViewModel
class MainScreenViewModel @Inject constructor(private val appRepository: AppRepository)
    : ViewModel(){

    private val TAG = "MainViewModel"
    private val onFailureText = "onFailure: "

    var mainScreenShowProgressIndicator = MutableStateFlow(false)
    var mainScreenProgressBarFactor = MutableStateFlow<Float>(0.0f)
    var mainScreenProgressBarPercent = MutableStateFlow<Int>(0)

    var isMainScreenButtonsEnabled = MutableStateFlow(true)

    val isNoInternetConnectionDialogVisible = MutableStateFlow(false)

    fun getBooks(context: Context) {

        viewModelScope.launch(Dispatchers.Main) {

            mainScreenProgressBarFactor.update{ currentValue -> 0.0f }
            mainScreenProgressBarPercent.update{ currentValue -> 0 }
            isMainScreenButtonsEnabled.update{ currentValue -> false }
            mainScreenShowProgressIndicator.update{ currentValue ->true }

            viewModelScope.launch(Dispatchers.IO) {

                try {

                    val booksDataResponse = appRepository.getBooks()

                    if (booksDataResponse.isSuccessful) {

                        val booksData = booksDataResponse.body()

                        val books = booksData?.results?.books
                        addBooksToDatabase(books, context)

                    } else {

                        val failureMessageToLog = "Response Error!"
                        handleError(failureMessageToLog, context, false)

                    }

                } catch (exception: Exception) {

                    val failureMessageToLog = exception.message.toString()

                    if (exception is NoInternetConnectionException) {
                        handleError(failureMessageToLog, context, true)
                    } else {
                        handleError(failureMessageToLog, context, false)
                    }

                }

            }
        }
    }

    private fun handleError(failureMessageToLog: String, context: Context, showNoInternetConnectionPopup: Boolean) {

        Log.d(TAG, onFailureText + failureMessageToLog)

        viewModelScope.launch(Dispatchers.Main) {

            if(!showNoInternetConnectionPopup){
                Toast.makeText(
                    context,
                    R.string.retrofit_error_message,
                    Toast.LENGTH_LONG
                ).show()
            }

            mainScreenShowProgressIndicator.update{ currentValue -> false }
            isMainScreenButtonsEnabled.update{ currentValue ->  true }
            isNoInternetConnectionDialogVisible.update{ currentValue -> showNoInternetConnectionPopup }


        }

    }

    private fun addBooksToDatabase(
        books: ArrayList<Book>?,
        context: Context
    ) {
        if (books.isNullOrEmpty()) {

            handleEndOfProcess(R.string.main_screen_no_books_message, context)

        } else {

            val mutex = Mutex()

            val sizeOfbooks = books.size
            var index = 0

            viewModelScope.launch(Dispatchers.IO) {
                for (book in books) {

                    if (book.primaryIsbn13 != null) {

                        var appBook = AppBook()
                        var smallImage = SmallImage()
                        var bigImage = BigImage()
                        var favoriteBookLabel = FavoriteBookLabel()

                        appBook.primaryIsbn13 = book.primaryIsbn13!!
                        smallImage.primaryIsbn13 = book.primaryIsbn13!!
                        bigImage.primaryIsbn13 = book.primaryIsbn13!!
                        favoriteBookLabel.primaryIsbn13 = book.primaryIsbn13!!

                        appBook.bookImage = book.bookImage
                        appBook.bookImageWidth = book.bookImageWidth
                        appBook.bookImageHeight = book.bookImageHeight
                        appBook.title = book.title
                        appBook.author = book.author
                        appBook.rank = book.rank
                        appBook.description = book.description
                        appBook.publisher = book.publisher

                        appRepository.addAppBook(appBook)

                        val bitmapBig: Bitmap =
                            Picasso.get().load(appBook.bookImage).resize(
                                ImageConstants.BigImageWidth, 0
                            ).get()
                        bigImage.bigImage = bitmapBig
                        appRepository.addBigImage(bigImage)

                        val bitmapSmall: Bitmap = Bitmap.createScaledBitmap(
                            bitmapBig,
                            ImageConstants.SmallImageWidth,
                            bitmapBig.height * ImageConstants.SmallImageWidth / bitmapBig.width,
                            false
                        )
                        smallImage.smallImage = bitmapSmall
                        appRepository.addSmallImage(smallImage)

                        favoriteBookLabel.favorite = false
                        appRepository.insertFavoriteBookLabel(favoriteBookLabel)

                        mutex.withLock {
                            index++
                            viewModelScope.launch(Dispatchers.Main) {
                                Log.d(
                                    TAG,
                                    "Factor: " + mainScreenProgressBarFactor.value.toString()
                                )
                                mainScreenProgressBarFactor.update{ currentValue ->
                                    index.toFloat() / sizeOfbooks.toFloat() }
                                mainScreenProgressBarPercent.update{ currentValue ->
                                    (mainScreenProgressBarFactor.value * 100).toInt() }

                            }

                            if (index == sizeOfbooks) {

                                handleEndOfProcess(R.string.main_activity_books_downloaded_message, context)

                            }

                        }
                    }
                }
            }


        }
    }

    private fun handleEndOfProcess(messageTextId: Int, context: Context) {
        viewModelScope.launch(Dispatchers.Main) {

            mainScreenProgressBarFactor.update{ currentValue -> 1.0f }
            mainScreenProgressBarPercent.update{ currentValue -> 100 }

            Toast.makeText(
                context,
                messageTextId,
                Toast.LENGTH_LONG
            ).show()

            mainScreenShowProgressIndicator.update{ currentValue -> false }
            isMainScreenButtonsEnabled.update{ currentValue -> true }

        }
    }

}