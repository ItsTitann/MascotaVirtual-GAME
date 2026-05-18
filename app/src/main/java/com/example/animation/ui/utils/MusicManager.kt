package com.example.animation.ui.utils

import android.content.Context
import android.media.MediaPlayer
import com.example.animation.R

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null
    private var currentResId: Int = -1
    
    // Volúmenes (0.0 a 1.0)
    var musicVolume: Float = 0.8f
        set(value) {
            field = value
            mediaPlayer?.setVolume(value, value)
        }
        
    var sfxVolume: Float = 1.0f

    fun playMusic(context: Context, resId: Int, loop: Boolean = true) {
        if (currentResId == resId && mediaPlayer?.isPlaying == true) return

        stopMusic()

        mediaPlayer = MediaPlayer.create(context.applicationContext, resId).apply {
            isLooping = loop
            setVolume(musicVolume, musicVolume)
            start()
        }
        currentResId = resId
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentResId = -1
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
    }

    fun resumeMusic() {
        mediaPlayer?.start()
    }

    // Nueva función para efectos de sonido cortos (SFX)
    fun playSound(context: Context, resId: Int) {
        MediaPlayer.create(context.applicationContext, resId).apply {
            setVolume(sfxVolume, sfxVolume)
            setOnCompletionListener { 
                it.release() // Liberar memoria al terminar el sonido
            }
            start()
        }
    }
}
