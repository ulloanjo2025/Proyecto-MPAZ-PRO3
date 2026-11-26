// Desarrollado por: Jonathan Ulloa (util)
package cl.duoc.mpazest3.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import cl.duoc.mpazest3.R

object SoundManager {
    private var soundPool: SoundPool? = null
    private var soundClick = 0
    private var soundWin = 0
    private var soundFail = 0

    // Inicializar el motor de audio con la carpeta raw/
    fun init(context: Context) {
        if (soundPool != null) return

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attributes)
            .build()

        soundPool?.let { pool ->
            // Carga tus audios descargados desde res/raw/
            try {
                soundClick = pool.load(context, R.raw.click_sound, 1)
                soundWin = pool.load(context, R.raw.win_trumpet, 1)
                soundFail = pool.load(context, R.raw.fail_cue, 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playClick() {
        if (soundClick != 0) soundPool?.play(soundClick, 1f, 1f, 0, 0, 1f)
    }

    fun playWin() {
        if (soundWin != 0) soundPool?.play(soundWin, 1f, 1f, 0, 0, 1f)
    }

    fun playFail() {
        if (soundFail != 0) soundPool?.play(soundFail, 1f, 1f, 0, 0, 1f)
    }
}