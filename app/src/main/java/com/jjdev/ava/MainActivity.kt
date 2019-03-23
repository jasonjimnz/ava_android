package com.jjdev.ava

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.github.kittinunf.fuel.android.extension.responseJson
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var avaTTS: TextToSpeech? = null
    private var listenBtn: Button? = null
    private var speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    private var layoutView: View? = null
    private var textResult: TextView? = null
    private var talkBtn: Button? = null
    private var api: Api = Api()
    private var homeBtn: Button? = null
    private var mapsBtn: Button? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            //TODO: Text to speech listo
            //avaTTS?.speak("Hola mundo", TextToSpeech.QUEUE_FLUSH, null)
            //Toast.makeText(this, "Hablando", Toast.LENGTH_LONG).show()
        } else {
            //TODO: gestionar el error
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listenBtn = findViewById<Button>(R.id.button)
        talkBtn = findViewById(R.id.button2);
        textResult = findViewById<TextView>(R.id.textView)
        mapsBtn = findViewById(R.id.button8)

        listenBtn?.setOnClickListener {
            Toast.makeText(this, "Testing Button", Toast.LENGTH_SHORT).show()
            val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Pregúntame algo")
            try {
                startActivityForResult(speechIntent, 100)
            } catch (a: ActivityNotFoundException){
                Toast.makeText(applicationContext, "Grabación de voz no soportada", Toast.LENGTH_SHORT).show()
            }
        }
        talkBtn?.setOnClickListener {
            Toast.makeText(this, "Escuchando", Toast.LENGTH_SHORT).show()
            avaTTS?.speak(textResult?.text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
        }
        // TTS Check
        /*var ttsIntent = Intent()
        ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(ttsIntent, 1000)*/

        homeBtn = findViewById(R.id.button5);
        homeBtn?.setOnClickListener {
            var intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        mapsBtn?.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // For hiding the keyboard
        var im = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(layoutView?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        when (requestCode) {
            100 -> {
                if (resultCode == RESULT_OK && null != data) {

                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    textResult?.text = (result[0].toString())

                    //TODO: Call API
                    api.sendText(result[0].toString()).responseJson { _, _, resultJ ->
                        val (bytes, error) = resultJ
                        if (bytes != null){
                            textResult?.text = resultJ.get().obj().get("response").toString()
                            avaTTS?.speak(textResult?.text.toString(),  TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                        if (error != null){
                            Log.e("ERRORCALL", error.toString())
                            Toast.makeText(this, "Error llamando", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Toast.makeText(this, result[0], Toast.LENGTH_LONG).show()
                }
            }
            1000 ->{
                // TODO: Texto a  voz
                avaTTS = TextToSpeech(this, this)
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                    avaTTS = TextToSpeech(this, this)
                } else {
                    val installIntent = Intent()
                    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
                    startActivity(installIntent)
                }
            }
        }
    }
}
