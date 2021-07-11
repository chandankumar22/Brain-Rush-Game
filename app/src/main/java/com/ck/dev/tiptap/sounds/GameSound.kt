package com.ck.dev.tiptap.sounds

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.annotation.RawRes
import com.ck.dev.tiptap.R
import com.ck.dev.tiptap.helpers.SharedPreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

object GameSound {


    //private var gameBgSoundId: Int = soundPool.load(context, R.raw.game_bg_sound, 1)

    suspend fun Context.playBtnClickSound() {
        Timber.i("playBtnClickSound called")
        if(SharedPreferenceHelper.isSoundsOn){
            val soundPool = getSoundPool()
            /*val player = MediaPlayer.create(this, R.raw.btn_click)
            player.start()*/
            val id: Int = soundPool.loadSound(this, R.raw.btn_click)
            withContext(Dispatchers.IO){
                soundPool.setOnLoadCompleteListener { soundPool, _, _ ->
                    Timber.i("sample ready")
                    soundPool.play(id, 1f, 1f, 1, 0, 1f)
                }
            }
        }
    }

    fun Context.playTimerSound(): MediaPlayer? {
        if(SharedPreferenceHelper.isSoundsOn){
            Timber.i("playBtnClickSound called")
            val soundPool = getSoundPool()
            val player = MediaPlayer.create(this, R.raw.timer)
            player.start()
            return player
            /*val id: Int = soundPool.loadSound(this, R.raw.timer)
            withContext(Dispatchers.IO){
                soundPool.setOnLoadCompleteListener { soundPool, _, _ ->
                    Timber.i("sample ready")
                    soundPool.play(id, 1f, 1f, 1, 0, 1f)
                }
            }*/
        }
        return null
    }

    suspend fun Context.playFindTheNumSuccess() {
        if(SharedPreferenceHelper.isSoundsOn){
            Timber.i("playBtnClickSound called")
            val soundPool = getSoundPool()
            /*val player = MediaPlayer.create(this, R.raw.btn_click)
            player.start()*/
            val id: Int = soundPool.loadSound(this, R.raw.find_the_num_success)
            withContext(Dispatchers.IO){
                soundPool.setOnLoadCompleteListener { soundPool, _, _ ->
                    Timber.i("sample ready")
                    soundPool.play(id, 1f, 1f, 1, 0, 1f)
                }
            }
        }
    }

    suspend fun Context.playFindTheNumWrong() {
        if(SharedPreferenceHelper.isSoundsOn){
            Timber.i("playBtnClickSound called")
            val soundPool = getSoundPool()
            /*val player = MediaPlayer.create(this, R.raw.btn_click)
            player.start()*/
            val id: Int = soundPool.loadSound(this, R.raw.find_the_num_lose)
            withContext(Dispatchers.IO){
                soundPool.setOnLoadCompleteListener { soundPool, _, _ ->
                    Timber.i("sample ready")
                    soundPool.play(id, 1f, 1f, 1, 0, 1f)
                }
            }
        }
    }

    suspend fun Context.playLevelFinish() {
        if(SharedPreferenceHelper.isSoundsOn){
            Timber.i("playBtnClickSound called")
            val soundPool = getSoundPool()
            /*val player = MediaPlayer.create(this, R.raw.btn_click)
            player.start()*/
            val id: Int = soundPool.loadSound(this, R.raw.level_finish)
            withContext(Dispatchers.IO){
                soundPool.setOnLoadCompleteListener { soundPool, _, _ ->
                    Timber.i("sample ready")
                    soundPool.play(id, 1f, 1f, 1, 0, 1f)
                }
            }
        }
    }

    suspend fun Context.playRtcWrong() {
        if(SharedPreferenceHelper.isSoundsOn){
            Timber.i("playBtnClickSound called")
            val soundPool = getSoundPool()
            /*val player = MediaPlayer.create(this, R.raw.btn_click)
            player.start()*/
            val id: Int = soundPool.loadSound(this, R.raw.rtc_wrong)
            withContext(Dispatchers.IO){
                soundPool.setOnLoadCompleteListener { soundPool, _, _ ->
                    Timber.i("sample ready")
                    soundPool.play(id, 1f, 1f, 1, 0, 1f)
                }
            }
        }
    }

    suspend fun Context.playRtcSuccess() {
        if(SharedPreferenceHelper.isSoundsOn){
            Timber.i("playBtnClickSound called")
            val soundPool = getSoundPool()
            /*val player = MediaPlayer.create(this, R.raw.btn_click)
            player.start()*/
            val id: Int = soundPool.loadSound(this, R.raw.rtc_success)
            withContext(Dispatchers.IO){
                soundPool.setOnLoadCompleteListener { soundPool, _, _ ->
                    Timber.i("sample ready")
                    soundPool.play(id, 1f, 1f, 1, 0, 1f)
                }
            }
        }
    }

    suspend fun Context.playFlipCard() {
        if(SharedPreferenceHelper.isSoundsOn){
            Timber.i("playFlipCard called")
            val soundPool = getSoundPool()
            /*val player = MediaPlayer.create(this, R.raw.btn_click)
            player.start()*/
            val id: Int = soundPool.loadSound(this, R.raw.card_flip)
            withContext(Dispatchers.IO){
                soundPool.setOnLoadCompleteListener { soundPool, _, _ ->
                    Timber.i("sample ready")
                    soundPool.play(id, 1f, 1f, 1, 0, 1f)
                }
            }
        }
    }

    suspend fun Context.playLevelLose() {
        if(SharedPreferenceHelper.isSoundsOn){
            Timber.i("playLevelLose called")
            val soundPool = getSoundPool()
            /*val player = MediaPlayer.create(this, R.raw.btn_click)
            player.start()*/
            val id: Int = soundPool.loadSound(this, R.raw.level_lose)
            withContext(Dispatchers.IO){
                soundPool.setOnLoadCompleteListener { soundPool, _, _ ->
                    Timber.i("sample ready")
                    soundPool.play(id, 1f, 1f, 1, 0, 1f)
                }
            }
        }
    }

    suspend fun Context.playJwWordTap() {
        if(SharedPreferenceHelper.isSoundsOn){
            Timber.i("playJwWordTap called")
            val soundPool = getSoundPool()
            /*val player = MediaPlayer.create(this, R.raw.btn_click)
            player.start()*/
            val id: Int = soundPool.loadSound(this, R.raw.jw_tap_word)
            withContext(Dispatchers.IO){
                soundPool.setOnLoadCompleteListener { soundPool, _, _ ->
                    Timber.i("sample ready")
                    soundPool.play(id, 1f, 1f, 1, 0, 1f)
                }
            }
        }
    }

    suspend fun Context.playJwWordSuccess() {
        if(SharedPreferenceHelper.isSoundsOn){
            Timber.i("playJwWordSuccess called")
            val soundPool = getSoundPool()
            /*val player = MediaPlayer.create(this, R.raw.btn_click)
            player.start()*/
            val id: Int = soundPool.loadSound(this, R.raw.jw_success)
            withContext(Dispatchers.IO){
                soundPool.setOnLoadCompleteListener { soundPool, _, _ ->
                    Timber.i("sample ready")
                    soundPool.play(id, 1f, 1f, 1, 0, 1f)
                }
            }
        }
    }

    suspend fun Context.playJwWordFail() {
        if(SharedPreferenceHelper.isSoundsOn){
            Timber.i("playJwWordFail called")
            val soundPool = getSoundPool()
            /*val player = MediaPlayer.create(this, R.raw.btn_click)
            player.start()*/
            val id: Int = soundPool.loadSound(this, R.raw.jw_fail)
            withContext(Dispatchers.IO){
                soundPool.setOnLoadCompleteListener { soundPool, _, _ ->
                    Timber.i("sample ready")
                    soundPool.play(id, 1f, 1f, 1, 0, 1f)
                }
            }
        }
    }

    suspend fun Context.playGameBgSound() {
        Timber.i("playGameBgSound called")
        val soundPool = getSoundPool()
        val id: Int = soundPool.loadSound(this, R.raw.game_bg_sound)
        withContext(Dispatchers.IO){
            soundPool.setOnLoadCompleteListener { soundPool, _, _ ->
                Timber.i("sample ready")
                soundPool.play(id, 1f, 1f, 1, -1, 1f)
            }
        }
    }

    fun getSoundPool(): SoundPool = SoundPool.Builder().setMaxStreams(10).build()

    fun SoundPool.loadSound(ctx:Context, @RawRes res:Int) = load(ctx, res, 1)

}