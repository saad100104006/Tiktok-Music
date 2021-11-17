package app.mocky.music.api

import app.mocky.music.modelclass.Shorts
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiInterface {

    @GET("v3/b9f74279-038b-4590-9f96-7c720261294c/")
    fun getMusicModelClassData():Observable<Shorts>
}