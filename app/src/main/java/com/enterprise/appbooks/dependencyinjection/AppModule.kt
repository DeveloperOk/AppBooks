package com.enterprise.appbooks.dependencyinjection

import android.content.Context
import com.enterprise.appbooks.interfaces.retrofitnytimes.NytimesApi
import com.enterprise.appbooks.localdatasource.database.BookDatabase
import com.enterprise.appbooks.remotedatasource.retrofit.nytimes.RetrofitNYTimes
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