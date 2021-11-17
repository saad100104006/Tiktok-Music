package app.mocky.music.activity

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.webkit.CookieManager
import androidx.annotation.NonNull
import java.io.File
import java.util.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import app.mocky.music.R
import app.mocky.music.database.MusicRepository
import app.mocky.music.modelclass.CurrentMusicDetails
import app.mocky.music.modelclass.Shorts
import android.content.BroadcastReceiver
import java.lang.Exception


class MusicService : Service() {
    var mediaPlayer: MediaPlayer? = null
    var isPlaying = false
    //This isFirsttime is after the download
    //This isFirsttime is before the download, This is to prevent pause and play causing a replay on first time
    var isFirsttimebd = true
    var isIncrease = true
    var isStop = false
    var currentMusic=0
    var currentTotalTime =0
    private val NOTIFICATION_ID = 555
    var musicRepository :MusicRepository? =null
    var shorts: Shorts? =null
    var currentProgress = 0
    var playNow=false



    @NonNull
    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
         musicRepository = MusicRepository(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder: Notification.Builder = Notification.Builder(this, createNotificationChannel("MusicMocky","app.mocky.music"))
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Music is Playing . . .")
                .setAutoCancel(true)
            val notification: Notification = builder.build()
            startForeground(NOTIFICATION_ID, notification)
        }
        else {
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("SmartTracker is Running...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
            val notification: Notification = builder.build()
            startForeground(NOTIFICATION_ID, notification)
        }
        val path =Environment.getExternalStorageDirectory().absolutePath+"/"+Environment.DIRECTORY_DOWNLOADS+"/tmp1.mp3"
        if (intent!=null){
            shorts = intent.getSerializableExtra("shorts") as Shorts
            if (intent.action =="Play"){
                ResumeSound()
            }
            else if (intent.action =="Pause"){
                PauseSound()
            }
            else if (intent.action =="Next"){
                Next()
            }
            else if (intent.action =="Prev"){
                Prev()
            }
            else if (intent.action =="EndAllProcess"){
                EndAllProcess()
            }
            else if (intent.action =="Speed"){
                Speed()
            }

        }
        if (currentMusic==0&&isFirsttimebd){
            val name = "tmp"+currentMusic+".mp3"
            downloadMusic(name,shorts!!.shorts[currentMusic].audioPath,applicationContext)

        }
        return START_STICKY

    }

    private fun Speed() {
        if (mediaPlayer != null) {
            val currentMusicDetailsService =  musicRepository!!.readCurrentMusicDetailsService()
            val speed = currentMusicDetailsService!!.speed
            mediaPlayer!!.setPlaybackParams(mediaPlayer!!.getPlaybackParams().setSpeed(speed))

        }
    }

    private fun ResumeSound() {

        if (mediaPlayer != null) {
            UpdatePlayStatus("1")
            Speed()
            mediaPlayer!!.start()
                isPlaying = true
            }
    }

    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context, intent: Intent) {
            if (isFirsttimebd) {
                val path =
                    getExternalFilesDir(null)!!.absolutePath + "/" + Environment.DIRECTORY_MUSIC + "/tmp" + currentMusic + ".mp3"
                UpdateDownLoadStatus("1")
                UpdateTitleAndDate(
                    shorts!!.shorts[currentMusic].title,
                    shorts!!.shorts[currentMusic].dateCreated
                )
                PlaySound(path)
                isIncrease = true
                isFirsttimebd = false
            } else {
                val path =
                    getExternalFilesDir(null)!!.absolutePath + "/" + Environment.DIRECTORY_MUSIC + "/tmp" + currentMusic + ".mp3"
                if (!mediaPlayer!!.isPlaying) {
                    UpdateDownLoadStatus("1")
                    UpdateTitleAndDate(
                        shorts!!.shorts[currentMusic].title,
                        shorts!!.shorts[currentMusic].dateCreated
                    )
                    PlaySound(path)
                    isIncrease = true
                } else if (playNow) {
                    UpdateDownLoadStatus("1")
                    UpdateTitleAndDate(
                        shorts!!.shorts[currentMusic].title,
                        shorts!!.shorts[currentMusic].dateCreated
                    )
                    PlaySound(path)
                    isIncrease = true
                    playNow = false
                } else {
                    mediaPlayer!!.setOnCompletionListener {
                        UpdateDownLoadStatus("1")
                        UpdateTitleAndDate(
                            shorts!!.shorts[currentMusic].title,
                            shorts!!.shorts[currentMusic].dateCreated
                        )
                        PlaySound(path)
                        isIncrease = true
                    }
                }
            }
        }
    }
    fun downloadMusic(name: String,url: String,context: Context){
        val musicpath =  getExternalFilesDir(null)!!.absolutePath+"/"+Environment.DIRECTORY_MUSIC+"/tmp"+currentMusic+".mp3"
        Log.d("Data",musicpath)
        UpdateDownLoadStatus("0")
        if (isFileExists(musicpath)){
            deleteFiles(musicpath)
        }
        val cookie = CookieManager.getInstance().getCookie(url)
        val downloadManager = context.getSystemService(Service.DOWNLOAD_SERVICE) as DownloadManager

        val downloadRequest = DownloadManager.Request(Uri.parse(url))
        downloadRequest.setTitle(name)
        downloadRequest.setDescription("Downloading Music . . .")
        downloadRequest.addRequestHeader("cookie",cookie)
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
        downloadRequest.setDestinationInExternalFilesDir(applicationContext,Environment.DIRECTORY_MUSIC,name)
        downloadManager.enqueue(downloadRequest)
        try {
            registerReceiver(onComplete,  IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }catch (e:Exception){
            e.printStackTrace()
        }



    }
    private fun isFileExists(filename: String): Boolean {
        val folder1 = File( filename)
        return folder1.exists()
    }

    private fun deleteFiles(filename: String): Boolean {
        val folder1 = File(filename)
        return folder1.delete()
    }
    private fun PlaySound(path: String) {
        UpdatePlayStatus("1")
            mediaPlayer = MediaPlayer.create(applicationContext, Uri.fromFile(File(path)))
            mediaPlayer!!.start()
            isPlaying = true
            isStop = false
        Speed()
        mediaPlayer!!.setOnCompletionListener {
                isPlaying = false
            }
            currentTotalTime =mediaPlayer!!.duration/1000
//            mediaPlayer!!.seekTo(i* 1000)
            val timer = Timer()
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (!isStop){
                        if (mediaPlayer!=null) {
                            val mCurrentPosition: Int = mediaPlayer!!.getCurrentPosition() / 1000
                            UpdateCurrentTime(mCurrentPosition)
                            downloadNext(mCurrentPosition)


                        }
                    }else{
                        timer.cancel()
                    }
                }
            }, 0, 1000)

    }

    private fun downloadNext(input: Int) {
        val progress = (input.toFloat()/currentTotalTime.toFloat())*100
        if (progress>75 && (currentMusic<shorts!!.shorts.size-1)){
            if (isIncrease){
                isIncrease=false
                currentMusic++
            val name = "tmp"+currentMusic+".mp3"
            downloadMusic(name,shorts!!.shorts[currentMusic].audioPath,applicationContext)
            }
        }

    }

    private fun PauseSound() {
        if (mediaPlayer!=null) {
            UpdatePlayStatus("0")

            mediaPlayer!!.pause()
            isPlaying = false
        }
    }
    private fun UpdateCurrentTime(input:Int) {
        val progress = (input.toFloat()/currentTotalTime.toFloat())*100
        var sec = input
        val min = input/60
        val tmin = currentTotalTime/60
        var tsec = 0
        var tsecString = "0:0"
        //CURRENT TIME REFORMATTER
        var secString ="00"
        if (sec>=60){
            sec %= 60
        }
        if (sec>9) {
             secString ="$sec"
        }
        else{
            secString ="0$sec"
        }
        //TOTAL TIME REFORMATTER
        if (currentTotalTime<=60){
            tsec = currentTotalTime
        }
        else {
            tsec = currentTotalTime % 60
        }
        if (tsec>9){
            tsecString = "$tsec"
        }
        else{
            tsecString = "0$tsec"
        }

        currentProgress = progress.toInt()
        val currentMusicDetailsService =  musicRepository!!.readCurrentMusicDetailsService()
        currentMusicDetailsService!!.seekTime = progress.toString()
        currentMusicDetailsService.currentTime = "0$min:$secString"
        currentMusicDetailsService.totalTime = "0$tmin:$tsecString"
        musicRepository!!.updateCurrentMusicDetails(currentMusicDetailsService!!)

    }

    private fun Next(){
        if (currentProgress>75 && (currentMusic<shorts!!.shorts.size-1)){
            StopSound()
            playNow=true
        }
        else if (currentProgress<75 && (currentMusic<shorts!!.shorts.size-1)){
            StopSound()
            currentMusic++
            val name = "tmp"+currentMusic+".mp3"
            downloadMusic(name,shorts!!.shorts[currentMusic].audioPath,applicationContext)

        }
    }
    private fun Prev(){
        if (currentMusic>0){
            StopSound()
            currentMusic--
            UpdateTitleAndDate(
                shorts!!.shorts[currentMusic].title,
                shorts!!.shorts[currentMusic].dateCreated
            )
            val path =  getExternalFilesDir(null)!!.absolutePath+"/"+Environment.DIRECTORY_MUSIC+"/tmp"+currentMusic+".mp3"
            PlaySound(path)

        }

    }
    private fun EndAllProcess(){
//        StopSound()
        stopSelf()
//        unregisterReceiver(onComplete)

    }
    private fun StopSound() {
        Log.d("Complete","stop")
        isStop = true

        if (mediaPlayer!=null) {
            UpdatePlayStatus("0")
            mediaPlayer!!.stop()
//            mediaPlayer!!.release()

        }
    }

    private fun UpdateTitleAndDate(title:String, date :String) {
        val currentMusicDetailsService =  musicRepository!!.readCurrentMusicDetailsService()
        currentMusicDetailsService!!.title = title
        currentMusicDetailsService.date = date
        musicRepository!!.updateCurrentMusicDetails(currentMusicDetailsService!!)
    }
    private fun UpdateDownLoadStatus(downloadStatus:String) {
        val currentMusicDetailsService =  musicRepository!!.readCurrentMusicDetailsService()
        currentMusicDetailsService!!.downloadStatus = downloadStatus
        musicRepository!!.updateCurrentMusicDetails(currentMusicDetailsService!!)
    }
    private fun UpdatePlayStatus(playStatus:String) {
        val currentMusicDetailsService =  musicRepository!!.readCurrentMusicDetailsService()
        currentMusicDetailsService!!.playStatus = playStatus
        musicRepository!!.updateCurrentMusicDetails(currentMusicDetailsService!!)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onDestroy() {
        StopSound()
        unregisterReceiver(onComplete)
        super.onDestroy()
    }
}