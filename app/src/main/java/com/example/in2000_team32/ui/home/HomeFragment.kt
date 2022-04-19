package com.example.in2000_team32.ui.home

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.percentlayout.widget.PercentRelativeLayout
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.in2000_team32.R
import com.example.in2000_team32.api.DataSourceRepository
import com.example.in2000_team32.api.LocationDataSource
import com.example.in2000_team32.api.NominatimLocationFromString
import com.example.in2000_team32.databinding.FragmentHomeBinding
import com.google.android.gms.location.LocationServices
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Suppress("DEPRECATION") class HomeFragment : Fragment() {
    var show = false
    private val current: LocalDateTime = LocalDateTime.now()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH", Locale.getDefault())
    private val formatted: Double = current.format(formatter).toDouble()
    private var uvBar = 50
    private lateinit var location: Location
    private var observersStarted = false
    private lateinit var locationManager: LocationManager
    var appVisible = false

    // This property is only valid between onCreateView and onDestroyView.
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

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
        //Dette starter opp hele applikasjonen - Vi kan pynte på syntaks og struktur senere, vi må ha det i resume fordi onCreated skjer før onResume
        startApp()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        var root: View

        //Check if user has internet connection
        if (!isNetworkAvailable()) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show()
            root = View(context)
        } else {
            root = binding.root
        }

        //SearchAdapter
        //Vars som trengs
        var searchQueryRecycler: RecyclerView = binding.searchQueryRecycler

        //RecyclerView initialisering og setting av adapter
        searchQueryRecycler.layoutManager = LinearLayoutManager(this.context)
        searchQueryRecycler.adapter = SearchAdapter(mutableListOf())

        //Observator for RecyclerViewModellen
        homeViewModel.getPlaces().observe(viewLifecycleOwner) {
            if (it != null) {
                println("Trigger warning!!!")
                //Check if it is not null and if it is not empty
                if (it.isNotEmpty()) {
                    println("Warning triggered")
                    //Set adapter
                    searchQueryRecycler?.adapter = SearchAdapter(it as MutableList<NominatimLocationFromString>)
                }
                println(it)
            }
        }


        //If som åpner og lukker søkefeltet
        val searchButton = binding.searchButton
        searchButton.setOnClickListener {
            if (show) {
                hideSearch()
            } else {
                showSearch()
            }
        }
        //End of if som åpner og lukker søkefeltet


        //Sett solkrem
        binding.imageViewSolkrem.setImageResource(R.drawable.solkrem_lang_50pluss)
        //End of sett solkrem

        //Sjekk om det er darkmode eller ikke og sett været
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                binding.vaermeldingSky.setImageResource(R.drawable.tordensky)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.vaermeldingSky.setImageResource(R.drawable.alleskyerh)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                binding.vaermeldingSky.setImageResource(R.drawable.tordenskyh)
            }
        }
        //End of sjekk om det er darkmode eller ikke og sett været

        //Ser på klokken og bytter blobb
        settBlobb()
        //End of se på klokken og bytter blobb

        //Start textlistener for senere
        startSearchListener()

        return root
    }

    fun startApp() {
        mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun showSearch() {
        show = true
        binding.EditTextAddress.requestFocus()
        activity?.let { showKeyboard(it) }
        binding.searchLayout1.animate().translationY(0F)
        binding.searchButton.setImageResource(R.drawable.ic_baseline_close_24)
    }

    fun hideSearch() {
        val searchDistance = resources.getDimensionPixelSize(R.dimen.searchDistance).toFloat()
        show = false
        binding.EditTextAddress.getText().clear()
        hideKeyboard()
        binding.searchLayout1.animate().translationY(searchDistance)
        binding.searchButton.setImageResource(R.drawable.ic_baseline_search_24)
    }

    fun startSearchListener() {
        //Listen for input from EditTextAddress and print it to console
        binding.EditTextAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                println("Text: $s")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            //Ingen debouncing atm, så ikke bruk denne for mye ellers får vi kvote-kjeft
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                homeViewModel.fetchPlaces(s.toString())
            }
        })
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

    val mPermissionResult = registerForActivityResult(RequestPermission()) { result ->
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

    val locationListener = LocationListener { l ->
        location = l
        println("Received a location")
        println(location)
        grabInfo()
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
        var wifiEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

        fun useWifi(){
            println("wifi is enabled")
            //Make toast with message that wifi is enabled and that the app will not work without gps
            Toast.makeText(context, "Wifi is WUBBA WUBBA", Toast.LENGTH_LONG).show()
            var l = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            if (l != null) {
                println("wifi is enabled and location is not null")
                location = l
                grabInfo()
            } else {
                println("wifi is enabled and location is null")
            }
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0f, locationListener)
        }

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
                } else {
                    println("Gps is not enabled but network is available and location is null")
                    useWifi();
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
                    useWifi();
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
            }
            else if(wifiEnabled){
                println("wifi is enabled")
                //Make toast with message that wifi is enabled and that the app will not work without gps
                Toast.makeText(context, "Wifi is WUBBA WUBBA", Toast.LENGTH_LONG).show()
                var l = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                if (l != null) {
                    println("wifi is enabled and location is not null")
                    location = l
                    grabInfo()
                } else {
                    println("wifi is enabled and location is null")
                    useWifi();
                }
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0f, locationListener)
            }
            else{
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

    //Setter UV pin og tekst i detjalert view
    fun setUvPin(f: Float) {
        val view: View = binding.imageViewUvPin
        val params = view.layoutParams as PercentRelativeLayout.LayoutParams
        val info = params.percentLayoutInfo
        info.startMarginPercent = f
        view.requestLayout()
    }

    fun setUvPinTekst(f: Float, d: Double) {
        val view: View = binding.textViewUvPinTall
        val params = view.layoutParams as PercentRelativeLayout.LayoutParams
        val info = params.percentLayoutInfo

        if (d >= 10.5) {
            info.startMarginPercent = f - 0.10f
        } else if (d <= 0.4) {
            info.startMarginPercent = 0.0f
            binding.textViewUvPinTall.gravity = Gravity.START
        } else {
            info.startMarginPercent = f - 0.05f
        }
        binding.textViewUvPinTall.text = d.toString()
        view.requestLayout()
    }

    fun setUvAlle(f: Float, d: Double) {
        setUvPin(f)
        setUvPinTekst(f, d)
    }

    fun setUvBar(uv: Int, d: Double) {
        when (uv) {
            0 -> setUvAlle(0.0f, d)
            1 -> setUvAlle(0.0818f, d)
            2 -> setUvAlle(0.1636f, d)
            3 -> setUvAlle(0.2454f, d)
            4 -> setUvAlle(0.3272f, d)
            5 -> setUvAlle(0.4090f, d)
            6 -> setUvAlle(0.4909f, d)
            7 -> setUvAlle(0.5727f, d)
            8 -> setUvAlle(0.6545f, d)
            9 -> setUvAlle(0.7363f, d)
            10 -> setUvAlle(0.8181f, d)
            11 -> setUvAlle(0.9f, d)
        }
    }
    //End of set UV pin og UV tekst

    fun startObserverne() {
        // Get UV data
        getActivity()?.let {
            homeViewModel.getUvData().observe(it) {
                binding.textUvi.setText(it.toString() + " uvi")
                setUvBar(it.roundToInt(), it)
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

    //Ser på klokken og bytter blobb
    fun settBlobb() {
        when (formatted) {
            0.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.pink_blob)
            1.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.pink_blob)
            2.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.pink_blob)
            3.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.pink_blob)
            4.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.pink_blob)
            5.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.pink_blob)
            6.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.purple_blob)
            7.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.purple_blob)
            8.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.purple_blob)
            9.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.green_blob)
            10.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.green_blob)
            11.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.green_blob)
            12.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.blue_blob)
            13.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.blue_blob)
            14.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.blue_blob)
            15.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.blue_blob)
            16.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.blue_blob)
            17.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.blue_blob)
            18.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.pink_blob)
            19.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.pink_blob)
            20.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.pink_blob)
            21.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.pink_blob)
            22.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.pink_blob)
            23.0 -> binding.vaermeldingBlob.setImageResource(R.drawable.pink_blob)
            else -> binding.vaermeldingBlob.setImageResource(R.drawable.blue_blob)
        }
    }
}