package com.jjdev.ava

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.github.kittinunf.fuel.android.extension.responseJson
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, com.google.android.gms.location.LocationListener {
    override fun onLocationChanged(p0: Location?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mMap: GoogleMap

    private var locationManager: LocationManager? = null
    // TODO: Location vars
    private var REQUEST_LOCATION_CODE = 101
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocation: Location? = null
    private var latitude: String? = null
    private var longitude: String? = null
    private var token: String? = null
    private var api: Api = Api()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        buildGoogleApiClient()
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        checkLocationPermission()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Location Permission already granted
            getLocation();
        } else {
            //Request Location Permission
            checkLocationPermission()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocation()
        val mark1 = LatLng(40.44793, -3.72236)
        mMap.addMarker(MarkerOptions().position(mark1).title("Tu posición"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mark1))
        val mark2 = LatLng(40.4519412, -3.7264443)
        mMap.addMarker(MarkerOptions().position(mark2).title("423ccb9dec844517911345ddc2fa4820"))
        val mark3 = LatLng(40.4519554, -3.7264729)
        mMap.addMarker(MarkerOptions().position(mark3).title("6157166a5d1d4f788d1bef308f2ef2c8"))
        val mark4 = LatLng(40.399420, -3.764457)
        mMap.addMarker(MarkerOptions().position(mark4).title("35d4247c5c324d72a8898abcfc081ac2"))
    }

    // Location functions
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation != null) {
            latitude = mLocation!!.latitude.toString()
            longitude = mLocation!!.longitude.toString()
            mMap.addMarker(MarkerOptions().position(LatLng(latitude!!.toDouble(), longitude!!.toDouble())).title("Posicion"))
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
