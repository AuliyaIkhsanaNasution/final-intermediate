package com.dicoding.picodiploma.storylensapp.view.maps

import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dicoding.picodiploma.storylensapp.view.ViewModelFactory
import com.dicoding.picodiploma.storylensapp.data.pref.dataStore
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.picodiploma.storylensapp.R
import android.content.Intent
import android.content.pm.PackageManager
import com.dicoding.picodiploma.storylensapp.view.main.MainActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.storylensapp.data.api.ApiConfig
import com.dicoding.picodiploma.storylensapp.data.repository.UserRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.dicoding.picodiploma.storylensapp.data.pref.UserPreference
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.storylensapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.GoogleMap
import com.dicoding.picodiploma.storylensapp.di.Injection
import com.google.android.gms.maps.model.MapStyleOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var userPreference: UserPreference
    private lateinit var binding: ActivityMapsBinding
    private val viewModelMaps: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this, ApiConfig().getApiService("token"))
    }
    private lateinit var userRepository: UserRepository
    private lateinit var mMap: GoogleMap

    companion object {
        private const val TAG = "MapsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(this.dataStore)
        userRepository = Injection.userRepositoryProvide(this, ApiConfig().getApiService("token"))
        checkUserSession()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Function to check user session
    private fun checkUserSession() {
        Log.d("MapsActivity", "Checking user session")
        lifecycleScope.launch {
            val userModel = userPreference.getSession().first()
            if (userModel.token.isNotEmpty()) {
                observeViewModel()
                viewModelMaps.getLocation()
            } else {
                Log.d("MapsActivity", "Failed checkuserSession()")
                showToast()
                startActivity(Intent(this@MapsActivity, MainActivity::class.java))
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // setup map UI
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true

        getMyLocation()
        setMapStyle()

    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    // Function to observe the view model and handle updates
    private fun observeViewModel() {
        val boundsBuilder = LatLngBounds.Builder()

        viewModelMaps.mapList.observe(this) { listStory ->
            listStory.forEach { data ->
                val latLng = LatLng(data.lat ?: 0.0, data.lon ?: 0.0)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(data.name)
                        .snippet(data.description)
                )
                boundsBuilder.include(latLng)
            }
            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    // Padding around the bounds
                    300
                )
            )
        }
    }

    private fun showToast() {
        Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show()
    }
}