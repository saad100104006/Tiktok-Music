package app.mocky.music.api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiInstance {
    companion object{
        //The Url to fetch the json object
        val baseUrl = "https://run.mocky.io/"
        fun getApiInstance():Retrofit{
            //fetch data
            val retrofit =Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            return retrofit
        }
    }
}