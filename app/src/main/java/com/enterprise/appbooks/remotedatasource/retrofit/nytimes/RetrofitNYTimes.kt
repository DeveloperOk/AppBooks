package com.enterprise.appbooks.remotedatasource.retrofit.nytimes

import com.enterprise.appbooks.constants.nytimes.NytimesApiConstants
import com.enterprise.appbooks.interfaces.retrofitnytimes.NytimesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNYTimes {

    companion object {

        fun getRetrofitNewYorkTimesApi(): NytimesApi{

            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(NytimesApiConstants.BaseUrl)
                .build()
                .create(NytimesApi::class.java)

        }

    }

}