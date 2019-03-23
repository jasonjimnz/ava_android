package com.jjdev.ava

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.github.kittinunf.fuel.android.extension.responseJson

class HomeActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    override fun onInit(status: Int) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var imageMic:ImageView? = null
    private var layoutView: View? = null
    private var textResult: TextView? = null
    private var avaTTS: TextToSpeech? = null
    private var api: Api = Api()
    private var emailField: EditText? = null
    private var passwordField: EditText? = null
    private var loginBtn: Button? = null
    private var registerBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        imageMic = findViewById(R.id.imageView)
        emailField = findViewById(R.id.editText)
        passwordField = findViewById(R.id.editText2)
        loginBtn = findViewById(R.id.button4)
        registerBtn = findViewById(R.id.button3)

        imageMic?.setOnClickListener {
            //TODO: Speech to text functionality
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

        loginBtn?.setOnClickListener {
            api.login(emailField?.text.toString(), passwordField?.text.toString()).responseJson { _, _, result ->
                val (bytes, error) = result
                if (bytes != null){
                    val token = result.get().obj().get("token").toString()
                    Toast.makeText(this, token, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeLoggedActivity::class.java)
                    intent.putExtra("token", token)
                    startActivity(intent)
                }
                if (error != null){

                }
            }
        }

        registerBtn?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // TTS Check
        var ttsIntent = Intent()
        ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(ttsIntent, 1000)
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
                            val responss_text = resultJ.get().obj().get("response").toString()
                            textResult?.text = responss_text

                            avaTTS?.speak(responss_text,  TextToSpeech.QUEUE_FLUSH, null, null)
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
                // TODO: Texto a voz
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
