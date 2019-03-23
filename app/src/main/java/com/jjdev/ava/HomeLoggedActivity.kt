package com.jjdev.ava

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.github.kittinunf.fuel.android.extension.responseJson
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.support.v7.app.AlertDialog

class HomeLoggedActivity : AppCompatActivity(), TextToSpeech.OnInitListener,  com.google.android.gms.location.LocationListener {
    override fun onLocationChanged(location: Location?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onInit(status: Int) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var imageMic: ImageView? = null
    private var layoutView: View? = null
    private var textResult: TextView? = null
    private var avaTTS: TextToSpeech? = null
    private var api: Api = Api()
    private var locationManager: LocationManager? = null
    // TODO: Location vars
    private var REQUEST_LOCATION_CODE = 101
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocation: Location? = null
    private var latitude: String? = null
    private var longitude: String? = null
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_logged)

        imageMic = findViewById(R.id.imageView2)

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

        token = intent.getStringExtra("token")
        Toast.makeText(this, token, Toast.LENGTH_SHORT).show()
        // TTS Check
        var ttsIntent = Intent()
        ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(ttsIntent, 1000)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        buildGoogleApiClient()
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
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //Location Permission already granted
                        getLocation();
                    } else {
                        //Request Location Permission
                        checkLocationPermission()
                    }

                    api.sendText(result[0].toString(), token, latitude, longitude).responseJson { _, _, resultJ ->
                        val (bytes, error) = resultJ
                        if (bytes != null){
                            val responss_text = resultJ.get().obj().get("response").toString()
                            textResult?.text = responss_text

                            avaTTS?.speak(responss_text,  TextToSpeech.QUEUE_FLUSH, null, null)

                            Toast.makeText(this, "${this.latitude} ${this.longitude}", Toast.LENGTH_SHORT).show()
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
    // Location functions
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation != null) {
            latitude = mLocation!!.latitude.toString()
            longitude = mLocation!!.longitude.toString()
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build()

        mGoogleApiClient!!.connect()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE)
                        })
                        .create()
                        .show()

            } else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE)
        }
    }
}
