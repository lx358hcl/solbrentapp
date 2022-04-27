package com.example.in2000_team32.ui.home

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
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
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.percentlayout.widget.PercentRelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.in2000_team32.R
import com.example.in2000_team32.api.*
import com.example.in2000_team32.databinding.FragmentHomeBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@Suppress("DEPRECATION") class HomeFragment : Fragment() {
    var show = false
    private val current: LocalDateTime = LocalDateTime.now()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH", Locale.getDefault())
    private val formatted: Double = current.format(formatter).toDouble()
    private var uvBar = 50
    private var uvIndex = 0
    private lateinit var location: Location
    private var observersStarted = false
    private lateinit var locationManager: LocationManager
    var appVisible = false

    // This property is only valid between onCreateView and onDestroyView.
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var dataSourceRepository : DataSourceRepository

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
        //Dette starter opp hele applikasjonen - Vi kan pynte på syntaks og struktur senere, vi må ha det i resume fordi onCreated skjer før onResum
        //Check if permissions are granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            startApp()
        }
        else{
            //Start app
            startApp()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        var root: View
        dataSourceRepository = DataSourceRepository(requireContext())

        /*
        * Slett koden under etterpå
        * */
        //val response = homeViewModel.fetchWeatherData(59.9, 10.7)
        //val sky = homeViewModel.getCurrentSky()
        //startObserverne()

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
        //
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
        //Check if city is null in shared preferences
        var chosenLocation : ChosenLocation? = dataSourceRepository.getChosenLocation()

        if(chosenLocation == null) {
            mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            //Make toast that user has to choose a city
            println("User has to choose a city")
        }
        else{
            println(chosenLocation)
            println("HURRA")
            if(chosenLocation == null){
                chosenLocation = ChosenLocation("", 0.0, 0.0)
            }
            grabInfo(chosenLocation)
        }
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
        }
        else {
            Log.e(TAG, "onActivityResult: PERMISSION DENIED")
            println("Permission denied")
            //Make a toast that the user has denied permission to use location
            Toast.makeText(context, "Du må tillate bruk av lokasjon for å bruke appen", Toast.LENGTH_LONG).show()
            //Vis en melding som sier at man bør ha stedsposisijoner på eller etlerann
        }
    }

    val locationListener = LocationListener { l ->
        location = l
        println("Received a location")
        println(location)
        grabInfo(ChosenLocation("", 0.0, 0.0))
    }

    fun grabInfo(chosenLocation : ChosenLocation) {
        var currentActivity = getActivity()
        if (currentActivity != null) {
            if(chosenLocation.city == ""){
                homeViewModel.fetchLocationData(location.latitude, location.longitude)
                homeViewModel.fetchWeatherData(location.latitude, location.longitude)

                if (observersStarted == false) {
                    startObserverne(ChosenLocation("", 0.0, 0.0))
                    observersStarted = true
                }
                //User has not chosen
            }
            else {
                //Print out chosen location
                var lat = chosenLocation.lat
                var lon = chosenLocation.lon
                if (lat != null && lon != null) {
                    homeViewModel.fetchLocationData(lat, lon)
                    homeViewModel.fetchWeatherData(lat, lon)
                }
                if (observersStarted == false) {
                    startObserverne(ChosenLocation(chosenLocation.city, lat, lon))
                    observersStarted = true
                }
            }
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
        }
        else {
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
        var gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        var networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var wifiEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)

        fun useWifi(){
            println("wifi is enabled")
            //Make toast with message that wifi is enabled and that the app will not work without gps

            Toast.makeText(context, "Wifi is WUBBA WUBBA", Toast.LENGTH_LONG).show()
            var l = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            if (l != null) {
                println("wifi is enabled and location is not null")
                location = l
                grabInfo(ChosenLocation("", 0.0, 0.0))
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
                    grabInfo(ChosenLocation("", 0.0, 0.0))
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
                    grabInfo(ChosenLocation("", 0.0, 0.0))
                } else {
                    println("Gps is enabled and location is null")
                    useWifi();
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
            } else if(wifiEnabled){
                println("wifi is enabled")
                //Make toast with message that wifi is enabled and that the app will not work without gps
                Toast.makeText(context, "Wifi is WUBBA WUBBA", Toast.LENGTH_LONG).show()
                var l = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                if (l != null) {
                    println("wifi is enabled and location is not null")
                    location = l
                    grabInfo(ChosenLocation("", 0.0, 0.0))
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

        if(d>11.0){
            info.startMarginPercent = f - 0.02f
            binding.textViewUvPinTall.text = "11+"
        } else if (d >= 10.5 && d <= 11.0) {
            info.startMarginPercent = f - 0.05f
            binding.textViewUvPinTall.text = d.toString()
        } else if (d <= 0.4) {
            info.startMarginPercent = 0.0f
            binding.textViewUvPinTall.gravity = Gravity.START
            binding.textViewUvPinTall.text = d.toString()
        } else {
            info.startMarginPercent = f - 0.05f
            binding.textViewUvPinTall.text = d.toString()
        }
        view.requestLayout()
    }

    fun setUvAlle(f: Float, d: Double) {
        setUvPin(f)
        setUvPinTekst(f, d)
    }

    fun calculateHemisphere(latitude: Number) : String{
        var hemisphere = "N"
        if(latitude.toDouble() < 0){
            hemisphere = "S"
        }
        return hemisphere
    }

    fun updateVitaminDInfo(fitztype : Number, uvindex: Int, latitude: Double, longitude: Double){
        var vitaminDDataSource = VitaminDDataSource()
        var hemisphere = calculateHemisphere(latitude)
        var sunBurnRes = vitaminDDataSource.calculateTimeTillSunBurn(fitztype.toFloat(), uvindex.toFloat())
        var vitaminDRes = vitaminDDataSource.calculateVitaminDUIPerHour(fitztype.toFloat(), hemisphere, uvindex.toFloat())

        println(uvindex.toFloat())
        println(sunBurnRes)
        println(vitaminDRes)
        
        binding.timeTillSunburn.text = sunBurnRes.toString()
        binding.vitaminDPerHour.text = vitaminDRes.toString()
    }

    fun updateSunscreen(uvIndex : Number){
        var roundedUvIndex = uvIndex.toDouble().roundToInt()
        //Ekstrem
        if(roundedUvIndex >= 11){
            binding.imageViewSolkrem.setImageResource(R.drawable.solkrem_lang_50pluss)
        }
        //Svært ekstrem
        else if(roundedUvIndex >= 8){
            binding.imageViewSolkrem.setImageResource(R.drawable.solkrem_lang_50)
        }
        //Sterk
        else if(roundedUvIndex >= 6){
            binding.imageViewSolkrem.setImageResource(R.drawable.solkrem_lang_30)
        }
        //Moderat
        else if(roundedUvIndex >= 3){
            binding.imageViewSolkrem.setImageResource(R.drawable.solkrem_lang_30)
        }
        //Lav
        else if(roundedUvIndex >= 0){
            binding.imageViewSolkrem.setImageResource(R.drawable.solkrem_lang_25)
        }
    }

    fun setUvBar(uv: Int, d: Double) {
        when (uv){
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
    fun startObserverne(chosenLocation : ChosenLocation) {
        var chosenLocation = chosenLocation
        // Get UV data
        getActivity()?.let {
            homeViewModel.getUvData().observe(it) {
                binding.textUvi.setText(it.toString() + " uvi")
                setUvBar(it.roundToInt(), it)
                uvIndex = it.roundToInt()
                updateSunscreen(uvIndex)
                var fitztype = dataSourceRepository.getFitzType() // Henter hudtype fra shared preferences, gir 0 hvis ikke satt
                updateVitaminDInfo(fitztype, uvIndex, chosenLocation.lat, chosenLocation.lon)
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

        // Update UV forecast graph
        getActivity()?.let {
        homeViewModel.getUvForecastData().observe(it) { uvDataForecast ->
            homeViewModel.getUvForecastStartTime().observe(it) { startTime ->
                    // Call UvForecastGraphView addData
                    val gv = binding.uvForecastGraph
                    gv.addData(uvDataForecast, startTime)
                }
            }
        }

        // Update current temp in card
        getActivity()?.let {
            homeViewModel.getCurrentTemp().observe(it) { temp ->
                val formatedTemp: String = "$temp °C"
                binding.detaljerTemperatur.setText(formatedTemp)
            }
        }

        //Update cloud-picture
        getActivity()?.let {
            homeViewModel.getCurrentSky().observe(it) { sky ->

                println("Sky status er : " + sky)
                //Oppdater sky-bilde, alle mulige verdier sky kan være: https://api.met.no/weatherapi/weathericon/2.0/documentation#!/data/get_legends_format
                when(sky){
                    "clearsky_day" -> binding.vaermeldingSky.setImageResource(R.drawable.sol)
                    "clearsky_night" -> binding.vaermeldingSky.setImageResource(R.drawable.sol) //Finner ingen bilde for natt
                    "clearsky_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.sol) //usikker med polartwilight
                    "cloudy" -> binding.vaermeldingSky.setImageResource(R.drawable.overskyeth)
                    "fair_day" -> binding.vaermeldingSky.setImageResource(R.drawable.solskyh)
                    "fair_night" -> binding.vaermeldingSky.setImageResource(R.drawable.solskyh) //Mangler bilde for natt
                    "fair_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.solskyh)
                    "fog" -> binding.vaermeldingSky.setImageResource(R.drawable.overskyeth) //Ingen fog funnet
                    "heavyrain" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "heavyrainandthunder" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh) //Kanskje en med lyn og regn?
                    "heavyrainshowers_day" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "heavyrainshowers_night" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "heavyrainshowers_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "heavyrainshowersandthunder_day" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "heavyrainshowersandthunder_night" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "heavyrainshowersandthunder_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "heavysleet" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh) //Sludd
                    "heavysleetandthunder" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "heavysleetshowers_day" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "heavysleetshowers_night" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "heavysleetshowers_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "heavysleetshowersandthunder_day" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "heavysleetshowersandthunder_night" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "heavysleetshowersandthunder_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "heavysnow" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "heavysnowandthunder" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "heavysnowshowers_day" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "heavysnowshowers_night" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "heavysnowshowers_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "heavysnowshowersandthunder_day" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "heavysnowshowersandthunder_night" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "heavysnowshowersandthunder_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "lightrain" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "lightrainandthunder" -> binding.vaermeldingSky.setImageResource(R.drawable.tordenskyh)
                    "lightrainshowers_day" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "lightrainshowers_night" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "lightrainshowers_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "lightrainshowersandthunder_day" -> binding.vaermeldingSky.setImageResource(R.drawable.tordenskyh)
                    "lightrainshowersandthunder_night" -> binding.vaermeldingSky.setImageResource(R.drawable.tordenskyh)
                    "lightrainshowersandthunder_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.tordenskyh)
                    "lightsleet" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "lightsleetandthunder" -> binding.vaermeldingSky.setImageResource(R.drawable.tordenskyh)
                    "lightsleetshowers_day" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "lightsleetshowers_night" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "lightsleetshowers_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "lightsnow_day" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "lightsnow_night" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "lightsnow_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "lightsnowandthunder" -> binding.vaermeldingSky.setImageResource(R.drawable.tordenskyh)
                    "lightsnowshowers_day" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "lightsnowshowers_night" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "lightsnowshowers_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "lightssleetshowersandthunder_day" -> binding.vaermeldingSky.setImageResource(R.drawable.tordenskyh)
                    "lightssleetshowersandthunder_night" -> binding.vaermeldingSky.setImageResource(R.drawable.tordenskyh)
                    "lightssleetshowersandthunder_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.tordenskyh)
                    "lightssnowshowersandthunder_day" -> binding.vaermeldingSky.setImageResource(R.drawable.tordenskyh)
                    "lightssnowshowersandthunder_night" -> binding.vaermeldingSky.setImageResource(R.drawable.tordenskyh)
                    "lightssnowshowersandthunder_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.tordenskyh)
                    "partlycloudy_day" -> binding.vaermeldingSky.setImageResource(R.drawable.solskyh)
                    "partlycloudy_night" -> binding.vaermeldingSky.setImageResource(R.drawable.solskyh)
                    "partlycloudy_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.solskyh)
                    "rain" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "rainandthunder" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh) //kanskje tordensky?
                    "rainshowers_day" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "rainshowers_night" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "rainshowers_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "rainshowersandthunder_day" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh) //kanskje tordensky
                    "rainshowersandthunder_night" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "rainshowersandthunder_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "sleet" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "sleetandthunder" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "sleetshowers_day" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "sleetshowers_night" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "sleetshowers_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "sleetshowersandthunder_day" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "sleetshowersandthunder_night" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "sleetshowersandthunder_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.regnskyh)
                    "snow" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "snowandthunder" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "snowshowers_day" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "snowshowers_night" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "snowshowers_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "snowshowersandthunder_day" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "snowshowersandthunder_night" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    "snowshowersandthunder_polartwilight" -> binding.vaermeldingSky.setImageResource(R.drawable.snoskyh)
                    else -> {
                        binding.vaermeldingSky.setImageResource(R.drawable.sol)
                    }
                }
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