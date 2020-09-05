package com.dm.tutorialfullscreenactivity401

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Un esempio di attività a schermo intero che mostra e nasconde l'interfaccia utente del sistema
 * (ovvero la barra di stato e la barra di navigazione / sistema) con l'interazione dell'utente.
 */
class FullscreenActivity : AppCompatActivity() {
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler()

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Rimozione ritardata della barra di stato e della barra di navigazione

        // Notare che alcune di queste costanti sono nuove a partire dall'API 16 (Jelly Bean)
        // e dall'API 19 (KitKat). È sicuro usarli, poiché sono inline in fase di compilazione
        // e non fanno nulla sui dispositivi precedenti.
        fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val showPart2Runnable = Runnable {
        // Visualizzazione ritardata degli elementi dell'interfaccia utente
        supportActionBar?.show()
        fullscreenContentControls.visibility = View.VISIBLE
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    /**
     * Listener tattile da utilizzare per i controlli dell'interfaccia utente nel layout per
     * ritardare l'occultamento dell'interfaccia utente del sistema. Questo per evitare che il
     * comportamento stridente dei controlli vada via durante l'interazione con l'interfaccia
     * utente dell'attività.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isFullscreen = true

        // Configura l'interazione dell'utente per mostrare o nascondere manualmente l'interfaccia utente del sistema.
        fullscreenContent = findViewById(R.id.fullscreen_content)
        fullscreenContent.setOnClickListener { toggle() }

        fullscreenContentControls = findViewById(R.id.fullscreen_content_controls)

        // Dopo aver interagito con i controlli dell'interfaccia utente,
        // ritardare qualsiasi operazione hide () pianificata per evitare che il comportamento
        // stridente dei controlli scompaia durante l'interazione con l'interfaccia utente.
        findViewById<Button>(R.id.dummy_button).setOnTouchListener(delayHideTouchListener)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Attiva l' hide() iniziale subito dopo la creazione dell'attività, per indicare
        // brevemente all'utente che i controlli dell'interfaccia utente sono disponibili.
        delayedHide(100)
    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Nascondi prima l'interfaccia utente
        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        // Pianifica un runnable per rimuovere la barra di stato e la barra di navigazione dopo un ritardo
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Mostra la barra di sistema
        fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        isFullscreen = true

        // Pianifica un runnable per visualizzare gli elementi dell'interfaccia utente dopo un ritardo
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Pianifica una chiamata a hide() in [delayMillis], annullamento di eventuali chiamate
     * programmate in precedenza.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Indica se l'interfaccia utente del sistema deve essere nascosta automaticamente dopo
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * Se [AUTO_HIDE] è impostato, il numero di millisecondi da attendere dopo l'interazione
         * dell'utente prima di nascondere l'interfaccia utente del sistema.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Alcuni dispositivi meno recenti richiedono un piccolo ritardo tra gli aggiornamenti
         * del widget dell'interfaccia utente e una modifica dello stato e della barra di navigazione.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}