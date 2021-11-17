package app.mocky.music.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.mocky.music.api.ApiInstance
import app.mocky.music.api.ApiInterface
import app.mocky.music.modelclass.CurrentMusicDetails
import app.mocky.music.modelclass.Shorts
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MusicRepository {
    private var musicDao: MusicDao? = null
    var musicModelClass: MutableLiveData<Shorts> = MutableLiveData()
    var  instance: MusicRepository? = null
    constructor(context: Context?) {
        val musicDatabase = MusicDatabase.getInstance(context!!)
        musicDao = musicDatabase!!.musicDao()
    }


    suspend fun readCurrentMusicDetails() : LiveData<CurrentMusicDetails>? {
        return musicDao!!.currentMusicDetails()
    }
    fun readCurrentMusicDetailsService() : CurrentMusicDetails? {
        return musicDao!!.currentMusicDetailsService()
    }

    suspend fun deleteCurrentMusicDetails() {
         musicDao!!.deleteCurrentMusicDetails()
    }
    fun updateCurrentMusicDetails(currentMusicDetails: CurrentMusicDetails) {
        musicDao!!.update(currentMusicDetails)
    }

    suspend fun deleteCurrentMusicDetails(currentMusicDetails: CurrentMusicDetails) {
        musicDao!!.delete(currentMusicDetails)
    }

    suspend fun  insertCurentMusicDetails(currentMusicDetails: CurrentMusicDetails){

        musicDao!!.insert(currentMusicDetails)
    }

    suspend fun getMusicModelClassObserver(): MutableLiveData<Shorts> {
        return musicModelClass
    }



    suspend fun requestApiCall() {
        val retroInstance  = ApiInstance.getApiInstance().create(ApiInterface::class.java)
        retroInstance.getMusicModelClassData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(getMusicModelClassObserverRx())
    }
    suspend fun getMusicModelClassObserverRx(): Observer<Shorts> {
        return object :Observer<Shorts>{
            override fun onComplete() {
                //hide progress indicator .
            }

            override fun onError(e: Throwable) {
                musicModelClass.postValue(null)
                Log.d("Data",e.message.toString())

            }

            override fun onNext(t: Shorts) {
                musicModelClass.postValue(t)
            }

            override fun onSubscribe(d: Disposable) {
                //start showing progress indicator.
            }
        }
    }





}