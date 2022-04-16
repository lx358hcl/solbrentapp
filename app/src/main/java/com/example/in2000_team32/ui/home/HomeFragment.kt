package com.example.in2000_team32.ui.home

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.percentlayout.widget.PercentRelativeLayout
import com.example.in2000_team32.R
import com.example.in2000_team32.databinding.FragmentHomeBinding
import com.google.android.gms.location.LocationServices
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt


class HomeFragment : Fragment() {
    var show = false
    private var _binding: FragmentHomeBinding? = null
    private val current: LocalDateTime = LocalDateTime.now()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH", Locale.getDefault())
    private val formatted: Double = current.format(formatter).toDouble()

    private var uvStyrke: Int = 11

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val searchButton = binding.searchButton

        searchButton.setOnClickListener {
            if (show) {
                hideSearch()
            } else {
                showSearch()
            }
        }




        //Sett solkrem

        binding.imageViewSolkrem.setImageResource(R.drawable.solkrem_lang_50pluss)

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

        //Ser på klokken og bytter blobb

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


        val view: View = root.findViewById(R.id.imageViewUvPin)
        val params = view.layoutParams as PercentRelativeLayout.LayoutParams
// This will currently return null, if it was not constructed from XML.
// This will currently return null, if it was not constructed from XML.
        val info = params.percentLayoutInfo
        info.startMarginPercent = 0.0818f
        view.requestLayout()









        fun getGeoLocation(activity: Activity) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                println("Dette blir trigga")
                grabInfo()
            }
        }

        //Dette starter opp hele applikasjonen - Vi kan pynte på syntaks og struktur senere
        var currentActivity = getActivity()
        if (currentActivity != null) {
            getGeoLocation(currentActivity)
        }

        //Usikker på hva denne gjør

        //Setter UV pin og tekst i detjalert view
        fun setUvPin(f: Float) {
            val view: View = root.findViewById(R.id.imageViewUvPin)
            val params = view.layoutParams as PercentRelativeLayout.LayoutParams
            val info = params.percentLayoutInfo
            info.startMarginPercent = f
            view.requestLayout()
        }

        fun setUvPinTekst(f: Float, i: Int) {
            val view: View = root.findViewById(R.id.textViewUvPinTall)
            val params = view.layoutParams as PercentRelativeLayout.LayoutParams
            val info = params.percentLayoutInfo
            info.startMarginPercent = f
            binding.textViewUvPinTall.text = i.toString()
            view.requestLayout()
        }

        fun setUvAlle(f: Float, i: Int) {
            setUvPin(f)
            setUvPinTekst(f, i)
        }

        fun setUvBar(uv: Int){
            when(uv){
                0 -> setUvAlle(0.0f, 0)
                1 -> setUvAlle(0.0818f, 1)
                2 -> setUvAlle(0.1636f, 2)
                3 -> setUvAlle(0.2454f, 3)
                4 -> setUvAlle(0.3272f, 4)
                5 -> setUvAlle(0.4090f, 5)
                6 -> setUvAlle(0.4909f, 6)
                7 -> setUvAlle(0.5727f, 7)
                8 -> setUvAlle(0.6545f, 8)
                9 -> setUvAlle(0.7363f, 9)
                10 -> setUvAlle(0.8181f, 10)
                11 -> setUvAlle(0.9f, 11)
            }
        }

        setUvBar(uvStyrke)





        return root
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

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyboard(activity: FragmentActivity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(
            activity.currentFocus!!.windowToken,
            InputMethodManager.SHOW_FORCED,
            0
        )
    }


    private val mPermissionResult = registerForActivityResult(RequestPermission()) { result ->
        if (result) {
            Log.e(TAG, "onActivityResult: PERMISSION GRANTED")
            grabInfo()
        } else {
            Log.e(TAG, "onActivityResult: PERMISSION DENIED")
            grabInfo()
            //Vis en melding som sier at man bør ha stedsposisijoner på eller etleranna
        }
    }

    fun startObserverne() {
        // Get UV data
        getActivity()?.let {
            homeViewModel.getUvData().observe(it) {
                binding.textUvi.setText(it.toString() + " uvi")
                uvStyrke = it.roundToInt()
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

    //Her supresser jeg fordi vi sjekker for dette allerede inni i mPermissionResult + wgaf om missingPermission lizm
    fun grabInfo() {
        var currentActivity = getActivity()
        if (currentActivity != null) {
            var fusedLocationClient = LocationServices.getFusedLocationProviderClient(currentActivity)
            if (ActivityCompat.checkSelfPermission(
                    currentActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    currentActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //Dette skjer hvis de ikke tillater GPS
                homeViewModel.fetchLocationData(61.1122408, 10.4386779)
                homeViewModel.fetchWeatherData(61.1122408, 10.4386779)
                startObserverne()
                return
            }
            fusedLocationClient.getLastLocation()
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    homeViewModel.fetchLocationData(location.latitude, location.longitude)
                    homeViewModel.fetchWeatherData(location.latitude, location.longitude)
                }
                //Defaulter til dette hvis gps er avslått
                else {
                    homeViewModel.fetchLocationData(61.1122408, 10.4386779)
                    homeViewModel.fetchWeatherData(61.1122408, 10.4386779)
                }
                startObserverne()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
