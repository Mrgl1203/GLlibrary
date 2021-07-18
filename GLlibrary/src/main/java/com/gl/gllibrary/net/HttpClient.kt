package com.gl.gllibrary.net

import android.util.Log
import com.gl.glcomponentlibrary.sample.base.BASEURL_WANANDROID
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object HttpClient {

    fun getOkHttpClient(): OkHttpClient {

        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor(object :HttpLoggingInterceptor.Logger{
                override fun log(message: String) {
                    Log.i("request", "request: ${message}")
                }
            }))
            .build()
        return client
    }

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .client(getOkHttpClient())
            .baseUrl(BASEURL_WANANDROID)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}