package app.mocky.music.activity

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import app.mocky.music.databinding.ActivityMainBinding
import app.mocky.music.viewmodel.MusicViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import app.mocky.music.R
import app.mocky.music.modelclass.CurrentMusicDetails
import app.mocky.music.modelclass.Shorts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat




class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel:  MusicViewModel
    var isPlaying = true
    var isSongStillLoading = true
    var updateDb = true
    var shorts:Shorts? =null
    var appExit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = MusicViewModel()
        viewModel = ViewModelProvider(this).get(MusicViewModel::class.java)
        setContentView(binding.root)
        if (checkIfAlreadyhavePermission()){
            loadAPIData()
            ViewUpdate()
            AllClicks()
            SwipeNavigation()
        }else{
            requestPermision()
        }




    }

    private fun requestPermision() {
        ActivityCompat.requestPermissions(this,
             arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun SwipeNavigation() {
        binding.main.setOnTouchListener(object: OnSwipeTouchListener(this@MainActivity) {
            override fun onSwipeUp() {
                super.onSwipeUp()
                if (!isSongStillLoading) {
                    viewModel.Next(this@MainActivity, shorts)
                    isPlaying = false
                }else{
                    //Next Song still loading, check internet speed
                }

            }
            override fun onSwipeDown() {
                super.onSwipeDown()
                viewModel.Prev(this@MainActivity,shorts)
            }
        })
    }
    private fun AllClicks() {
        binding.tryAgain.setOnClickListener {
            binding.loadAnim.playAnimation()
            loadAPIData()
        }
        binding.loadPlayListLayout.setOnClickListener {
            //Prevent clicking beneath layout
        }
        binding.playPause.setOnClickListener {
            if (isPlaying){
                viewModel.Pause(this,shorts)
                isPlaying = false
            }
            else{
                viewModel.Play(this,shorts)
                isPlaying = true
            }

        }
        binding.next.setOnClickListener {
            if (!isSongStillLoading) {
                viewModel.Next(this, shorts)
                isPlaying = false
            }else{
                //Next Song still loading, check internet speed
            }
        }
        binding.prev.setOnClickListener {
            viewModel.Prev(this,shorts)
            isPlaying = false
        }
        binding.speed.setOnClickListener {
            UpdateSpeed(shorts)
        }
    }
    private fun loadAPIData() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                viewModel.getMusicModelClassObserver(this@MainActivity).observe(this@MainActivity, Observer<Shorts>{
                    if(it != null) {
                            binding.loadPlayListLayout.visibility = View.GONE
                        shorts = it
                        viewModel.StartService(this@MainActivity,shorts)

                    }
                    else {
                        binding.tryAgain.visibility = View.VISIBLE
                        binding.loadAnim.pauseAnimation()
                        updateDb = false
                        Log.d("Data","Failed")
                    }
                })

            }
        }
    }
    private fun ViewUpdate() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
        viewModel.readCurrentMusicDetails(this@MainActivity)!!.observe(this@MainActivity, Observer<CurrentMusicDetails>{
            if(it != null) {
                binding.currentDuration.text = it.currentTime
                binding.dateCreated.text = it.date
                binding.musicDuration.text = it.totalTime
                binding.musicTitle.text = it.title
                binding.speed.text = it.speed.toString()+"x"
                binding.seekBar.progress = it.seekTime.toFloat().toInt()
                if (it.downloadStatus.equals("1")){
                    binding.progressCircular.visibility = View.GONE
                    isSongStillLoading = false
                }
                else{
                    binding.progressCircular.visibility = View.VISIBLE
                    isSongStillLoading = true
                }
                if (it.playStatus.equals("1")){
                    binding.playPause.background = getDrawable(R.drawable.ic_round_pause_round)
                     isPlaying = true
                }
                else{
                    binding.playPause.background = getDrawable(R.drawable.ic_round_play_arrow_24)
                     isPlaying = false
                }

            }
            else {
                InsertIntoDB()
            }
        })
            }
        }
    }
    private fun InsertIntoDB() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
        viewModel.insert(this@MainActivity)
            }
        }
    }
    private fun UpdateSpeed(shorts: Shorts?) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
        viewModel.updateSpeed(this@MainActivity,binding.speed.text.toString(),shorts)
            }
        }
    }
    override fun onBackPressed() {
        if (!isPlaying){
            EndAllProcess()
        }else if (appExit==0){
            appExit++
            Toast.makeText(this@MainActivity, "Press again to exit", Toast.LENGTH_SHORT)
                .show()
        }
        else {
            EndAllProcess()
        }
    }
    private fun EndAllProcess(){
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                viewModel.EndAllProcess(this@MainActivity)
                finish()
                exitProcess(0)
            }
        }
    }
    private fun checkIfAlreadyhavePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] === PackageManager.PERMISSION_GRANTED
                ) {

                    // permission was granted,
                    loadAPIData()
                    ViewUpdate()
                    AllClicks()
                    SwipeNavigation()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Permission denied,Please enable to read your External storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}