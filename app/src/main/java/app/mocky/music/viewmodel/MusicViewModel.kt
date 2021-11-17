package app.mocky.music.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.mocky.music.activity.MusicService
import app.mocky.music.database.MusicRepository
import app.mocky.music.modelclass.CurrentMusicDetails
import app.mocky.music.modelclass.Shorts

class MusicViewModel : ViewModel() {
    suspend fun getMusicModelClassObserver(context: Context): MutableLiveData<Shorts> {
        val musicRepository = MusicRepository(context)

        musicRepository.requestApiCall()

        return musicRepository.getMusicModelClassObserver()
    }

    suspend fun readCurrentMusicDetails(context: Context): LiveData<CurrentMusicDetails>? {
        val musicRepository = MusicRepository(context)
        return musicRepository.readCurrentMusicDetails()
    }

    suspend fun insert(context: Context) {
        val musicRepository = MusicRepository(context)
        musicRepository.insertCurentMusicDetails(
            CurrentMusicDetails(
                "00:00", "00:00", 1f, "0", " . . . ", " . . . ", "0", "0", "0"
            )
        )
    }

    suspend fun updateSpeed(context: Context, currentSpeed: String, shorts: Shorts?) {
        val musicRepository = MusicRepository(context)
        val currentMusicDetails = musicRepository.readCurrentMusicDetailsService()
        if (currentSpeed.equals("1.0x")) {
            currentMusicDetails!!.speed = 1.5f
            musicRepository.updateCurrentMusicDetails(currentMusicDetails)
        } else if (currentSpeed.equals("1.5x")) {
            currentMusicDetails!!.speed = 2f
            musicRepository.updateCurrentMusicDetails(currentMusicDetails)
        } else if (currentSpeed.equals("2.0x")) {
            currentMusicDetails!!.speed = 1f
            musicRepository.updateCurrentMusicDetails(currentMusicDetails)
        }
        //Notify Service
        val intent = Intent(context, MusicService::class.java)
        intent.putExtra("shorts", shorts)
        intent.action = "Speed"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun pauseMusic(context: Context, shorts: Shorts?) {
        val intent = Intent(context, MusicService::class.java)
        intent.putExtra("shorts", shorts)
        intent.action = "Pause"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun nextMusic(context: Context, shorts: Shorts?) {
        val intent = Intent(context, MusicService::class.java)
        intent.putExtra("shorts", shorts)
        intent.action = "Next"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun previousMusic(context: Context, shorts: Shorts?) {
        val intent = Intent(context, MusicService::class.java)
        intent.putExtra("shorts", shorts)
        intent.action = "Prev"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun endAllProcess(context: Context) {
        val intent = Intent(context, MusicService::class.java)
        context.stopService(intent)
    }

    fun playMusic(context: Context, shorts: Shorts?) {
        val intent = Intent(context, MusicService::class.java)
        intent.action = "Play"
        intent.putExtra("shorts", shorts)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun startMusicService(context: Context, shorts: Shorts?) {
        val intent = Intent(context, MusicService::class.java)
        intent.putExtra("shorts", shorts)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

}