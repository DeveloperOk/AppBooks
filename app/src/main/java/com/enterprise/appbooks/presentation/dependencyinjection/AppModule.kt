package com.enterprise.appbooks.presentation.dependencyinjection

import android.content.Context
import com.enterprise.appbooks.data.interfaces.retrofitnytimes.NytimesApi
import com.enterprise.appbooks.data.localdatasource.database.BookDatabase
import com.enterprise.appbooks.data.remotedatasource.retrofit.nytimes.RetrofitNYTimes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideBookDatabase(@ApplicationContext context: Context): BookDatabase {
        return BookDatabase.getDatabase(context)
    }


    @Provides
    @Singleton
    fun provideNytimesApi(@ApplicationContext context: Context): NytimesApi {
        return RetrofitNYTimes.getRetrofitNewYorkTimesApi(context)
    }


}