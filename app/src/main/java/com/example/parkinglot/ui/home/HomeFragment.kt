package com.example.parkinglot.ui.home

import android.app.Activity
import android.content.ContentValues
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.parkinglot.MainActivity
import com.example.parkinglot.ParqueaderoFirebase
import com.example.parkinglot.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings


@Suppress("DEPRECATION")
class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private lateinit var lastLocation: Location


    companion object {
        var mapFragment : SupportMapFragment?=null
        val TAG: String = HomeFragment::class.java.simpleName
        fun newInstance() = HomeFragment()
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        //val textView: TextView = root.findViewById(R.id.text_home)
        //homeViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        //})
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)

        val context = context as MainActivity
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if(!isNetwork()){
           Toast.makeText(getActivity(),"No hay conexión a internet para actualizar el mapa", Toast.LENGTH_LONG).show();

        }

        return root
    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val context = context as MainActivity
        // Add a marker in Sydney and move the camera

        map.getUiSettings().setZoomControlsEnabled(true)
        map.setOnMarkerClickListener(this)



        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { //aun no hay permisos
            ActivityCompat.requestPermissions(context,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)

        }
        else{ //si hay permisos
            setUpMap()
            centrarLocalizacion()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    setUpMap()
                    centrarLocalizacion()
                } else {
                    // permission denied, boo! Disable the
                    // pendiente
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun centrarLocalizacion(){
        val context = context as MainActivity
        // 1
        map.isMyLocationEnabled = true

// 2
        fusedLocationClient.lastLocation.addOnSuccessListener(context) { location ->
            // Got last known location. In some rare situations this can be null.
            // 3
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13f))
            }
        }
    }

    private fun setUpMap() {

        var sydney = LatLng(4.694836, -74.086209)
        val db = FirebaseFirestore.getInstance()
        // [END get_firestore_instance]
        // [START set_firestore_settings]
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
        // [END set_firestore_settings]
        // TODO: handle loggedInUser authentication
        db.collection("malls")
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty){
                    Log.w(ContentValues.TAG, "Error obteniendo parqueaderos de Firebase ")
                }
                else{
                    for (document in documents) {
                        val park: ParqueaderoFirebase = document.toObject(ParqueaderoFirebase::class.java)
                        if(park.lat!=null && park.long!=null){
                            sydney = LatLng(park.lat!!, park.long!!)
                            map.addMarker(
                                MarkerOptions().position(sydney)
                                    .title(park.name)
                            )
                        }
                    }
                }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    override fun onMarkerClick(p0: Marker?) = false

    fun isNetwork(): Boolean {
        val connectivityManager = context?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        // In here we return true if network is not null and Network is connected
        if(networkInfo != null && networkInfo.isConnected){
            return true
        }
        return false

    }

}
