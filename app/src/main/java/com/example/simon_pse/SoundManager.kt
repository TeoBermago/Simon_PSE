package com.example.simon_pse

import android.content.Context
import android.media.SoundPool

class SoundManager(context: Context) {
    // Crea un pool che può suonare fino a 4 suoni contemporaneamente
    private val soundPool = SoundPool.Builder().setMaxStreams(4).build()

    // Mappa per associare ogni carattere (es. 'R') al suo file audio caricato in memoria
    private val soundMap = mutableMapOf<Char, Int>()
    private var errorSoundId: Int = 0

    init {
        // Carichiamo i file in memoria all'avvio dell'app!
        soundMap['R'] = soundPool.load(context, R.raw.beep_r, 1)
        soundMap['G'] = soundPool.load(context, R.raw.beep_g, 1)
        soundMap['B'] = soundPool.load(context, R.raw.beep_b, 1)
        soundMap['M'] = soundPool.load(context, R.raw.beep_m, 1)
        soundMap['Y'] = soundPool.load(context, R.raw.beep_y, 1)
        soundMap['C'] = soundPool.load(context, R.raw.beep_c, 1)

        //errorSoundId = soundPool.load(context, R.raw.error, 1)
    }

    fun playColor(color: Char) {
        // Cerca l'ID del suono in base al colore e lo riproduce
        soundMap[color]?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun playError() {
        soundPool.play(errorSoundId, 1f, 1f, 1, 0, 1f)
    }

    // Libera la memoria quando l'app viene chiusa
    fun release() {
        soundPool.release()
    }
}