package com.lab.zicevents.data.api.songkick

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SongkickApi {

    companion object {

        private const val BASE_URL = "https://api.songkick.com/api/3.0/"

        fun create(): SongkickApiInterface {

            val logging = HttpLoggingInterceptor()

            logging.level = HttpLoggingInterceptor.Level.BASIC

            val httpClient = OkHttpClient().newBuilder()

            // add logging as last interceptor
            httpClient.addInterceptor(logging)

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(Gson().newBuilder().create()))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(httpClient.build())
                .build()

            return retrofit.create(SongkickApiInterface::class.java)
        }
    }
}