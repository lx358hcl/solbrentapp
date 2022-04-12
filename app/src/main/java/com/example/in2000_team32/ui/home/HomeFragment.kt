package com.example.in2000_team32.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.in2000_team32.R
import com.example.in2000_team32.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    var show = false
    private var _binding: FragmentHomeBinding? = null
    private var uvBar = 50
    private lateinit var location: Location
    private var observersStarted = false
    private lateinit var locationManager: LocationManager
    var appVisible = false

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    //OnPause are used to check if the app is in the foreground or not
    override fun onPause() {
        super.onPause()
        appVisible = false
    }


    //When user returns to activity
    //Se her for forklaring hvorfor den må plasseres her: https://cdn.djuices.com/djuices/activity-lifecycle.jpeg
    override fun onResume() {
        println("Calling resume method")
        super.onResume()
        //Dette starter opp hele applikasjonen - Vi kan pynte på syntaks og struktur senere
        startApp()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        var root : View

        //Check if user has internet connection
        if (!isNetworkAvailable()) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show()
            root = View(context)
        }
        else{
            root = binding.root
        }


        hideSearch()
        hideKeyboard()

        val searchButton = binding.searchButton
        val UVbar = binding.progressBar

        UVbar.setProgress(uvBar)

        searchButton.setOnClickListener {
            if (show) {
                hideSearch()
            } else {
                showSearch()
            }
        }

        binding.imageViewSolkrem.setImageResource(R.drawable.solkrem_lang_50pluss)

        return root
    }

    fun getGeoLocation(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            println("Dette blir trigga")
            grabInfo()
        }
    }

    fun startApp() {
        mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun showSearch() {
        var searchDistance = resources.getDimensionPixelSize(R.dimen.searchDistance).toFloat()
        show = true
        binding.EditTextAddress.requestFocus()
        activity?.let { showKeyboard(it) }
        binding.searchLayout1.animate().translationY(0F)
        binding.searchButton.setBackgroundResource(R.drawable.ic_baseline_close_24)
    }

    fun hideSearch() {
        var searchDistance = resources.getDimensionPixelSize(R.dimen.searchDistance).toFloat()
        show = false
        binding.EditTextAddress.getText().clear()
        hideKeyboard()
        binding.searchLayout1.animate().translationY(searchDistance)
        binding.searchButton.setBackgroundResource(R.drawable.ic_baseline_search_24)
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(activity: FragmentActivity) {
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(activity.currentFocus!!.windowToken, InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        locationManager.removeUpdates(locationListener)
    }

    private val mPermissionResult = registerForActivityResult(RequestPermission()) { result ->
        if (result) {
            Log.e(TAG, "onActivityResult: PERMISSION GRANTED")
            println("Getting permissions")
            getLocation()
        } else {
            Log.e(TAG, "onActivityResult: PERMISSION DENIED")
            getLocation()
            //Vis en melding som sier at man bør ha stedsposisijoner på eller etleranna
        }
    }

    private val locationListener = LocationListener { l ->
        location = l
        println("Received a location")
        println(location)
        grabInfo()
    }

    fun isNetworkAvailable(): Boolean {
        var currentActivity = getActivity()
        var result = false
        val cm = context?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        result = true
                    }
                }
            }
        } else {
            if (cm != null) {
                val activeNetwork = cm.activeNetworkInfo
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    } else if (activeNetwork.type == ConnectivityManager.TYPE_VPN) {
                        result = true
                    }
                }
            }
        }
        return result
    }

    fun getLocation() {
        var currentActivity = getActivity()

        //We check if we have permission to get location
        if (currentActivity?.let {
                ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED && currentActivity?.let {
                ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED) {
            println("Permissions arent grranted")

            //Show Dialog that user needs to grant permissions for the app to work


            return
        } else {
            println("Permissions are granted mofo")
            //Gå videre nedover
        }

        //Get Locationmanager
        //Gpsenabled og networkenabled er egentlig litt feil å si, det betyr egentlig mer typ hva som er tilgjengelig og ikke om den er enabled eller ikke
        locationManager = currentActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        var networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        //Check if internet is available and if gps is enabled if not ask user to enable it or use network provider if available and if not ask user to enable
        if (!isNetworkAvailable()) {
            //If this happens just add a spinner to the entire page and make it impossible to use because no internet is present
            println("No internet")

            //Show toast that no internet is available and that the app will not work without internet access and that the user should enable internet access
            Toast.makeText(context, "No internet available", Toast.LENGTH_LONG).show()
        }
        else {
            println("Internet is available")
            if (networkEnabled) {
                println("Gps is not enabled but network is available")
                var l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (l != null) {
                    println("Gps is not enabled but network is available and location is not null")
                    location = l
                    grabInfo()
                }
                else {
                    println("Gps is not enabled but network is available and location is null")
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
            }
            else if (gpsEnabled) {
                println("Gps is enabled")
                var l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (l != null) {
                    println("Gps is enabled and location is not null")
                    location = l
                    grabInfo()
                } else {
                    println("Gps is enabled and location is null")
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
            }
            else {
                //Show AlertDialog that LocationServices is disabled and that the user should enable it in settings or dismiss if they dont want to enable it
                println("Gps is not enabled and network is not available")
                var alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle("Location Services Disabled")
                alertDialog.setMessage("Please enable Location Services in settings")
                alertDialog.setPositiveButton("Enable", DialogInterface.OnClickListener { dialog, which ->
                    println("User wants to enable location services")
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                })
                alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    println("User does not want to enable location services")
                })
                alertDialog.show()

            }
        }
    }

    fun startObserverne() {
        // Get UV data
        getActivity()?.let {
            homeViewModel.getUvData().observe(it) {
                binding.textUvi.setText(it.toString() + " uvi")
            }
        }
        // Get weather message
        getActivity()?.let {
            homeViewModel.getWeatherMsg().observe(it) { wMsg ->
                binding.textSolstyrke.setText(wMsg)
            }
        }
        //Updates DetaljerAddresse to Location based on GeoLocation
        getActivity()?.let {
            homeViewModel.getLocationName().observe(it) { it ->
                binding.detaljerAddresse.setText(it.toString())
            }
        }
    }

    fun grabInfo() {
        var currentActivity = getActivity()
        if (currentActivity != null) {
            homeViewModel.fetchLocationData(location.latitude, location.longitude)
            homeViewModel.fetchWeatherData(location.latitude, location.longitude)

            if (observersStarted == false) {
                startObserverne()
                observersStarted = true
            }
            return
        }
    }
}

