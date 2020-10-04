package com.example.workoutcompanion.activities

import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.workoutcompanion.R
import com.progur.droidmelody.SongFinder
import kotlinx.android.synthetic.main.activity_music.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.ctx
import org.jetbrains.anko.imageResource
import java.net.URL

class MusicActivity : AppCompatActivity() {

    var albumArt: ImageView? = null


    var playButton: ImageButton? = null
    var shuffleButton: ImageButton? = null

    var songTitle: TextView? = null
    var songArtist : TextView? = null

    var SongList:MutableList<SongFinder.Song>? = null
    var number :Int  = 0

    private var mediaPlayer: MediaPlayer? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)



        if ((Build.VERSION.SDK_INT>=23 && ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),0)

        }
        Log.d("Permi","")

        album_Art1.pauseAnimation()
        Play_btn.isEnabled = false

        GlobalScope.launch(Dispatchers.Main)  {

            async {
                createPlayer()
            }

        }




        Play_btn.setOnClickListener {
            playOrPause()
        }
        Go_btn.setOnClickListener {
            playRandom()
        }
    }



    private fun createPlayer(): MutableList<SongFinder.Song>? {


        val songFinder = SongFinder(contentResolver)
        songFinder.prepare()
        val songs = songFinder.allSongs

        // Play_btn.isEnabled = true
        //Go_btn.isEnabled = true



        number = songs?.count()!!

        return songs

    }

    fun playRandom() {
//println(number)
        Log.d("Songs!", "${createPlayer()?.count()}")
        if (createPlayer()?.count()!! != 0) {


        var randomNum = (0..createPlayer()?.count()!! - 1).shuffled().first()
        createPlayer()?.shuffle()
        Log.d("size", "$randomNum")
        println(number)
        println("GGG  ${createPlayer()}")

        var song = createPlayer()?.get(randomNum)


        mediaPlayer?.reset()
        if (song != null) {
            mediaPlayer = MediaPlayer.create(ctx, song.uri)

            mediaPlayer?.setOnCompletionListener {
                Play_btn?.imageResource = R.drawable.ic_baseline_play_arrow_24
                album_Art1.pauseAnimation()
            }

            //album_Art?.imageURI = song.albumArt


            song_title?.text = song.title


            song_Artist?.text = song.artist
            mediaPlayer?.start()
            playButton?.imageResource = R.drawable.ic_baseline_pause_24


        } else {


            Toast.makeText(this, "NoSong", Toast.LENGTH_SHORT).show()
        }

        Play_btn.isEnabled = true
        Play_btn?.imageResource = R.drawable.ic_baseline_pause_24
        album_Art1.playAnimation()
    } else {
            println(",,,,,${createPlayer()?.count()}")
            Toast.makeText(this, "Sorry no Music Found in your storage", Toast.LENGTH_SHORT).show()
        }

    }

    fun playOrPause(){
        var songPlaying:Boolean? = mediaPlayer?.isPlaying

        if(songPlaying == true){
            album_Art1.pauseAnimation()
            mediaPlayer?.pause()
            Play_btn?.imageResource = R.drawable.ic_baseline_play_arrow_24
        }
        else{
            album_Art1.playAnimation()
            mediaPlayer?.start()
            Play_btn?.imageResource = R.drawable.ic_baseline_pause_24
        }
    }







    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}