package com.example.in2000_team32.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.in2000_team32.R
import com.example.in2000_team32.databinding.FragmentHomeBinding
import com.google.android.gms.location.LocationServices


class HomeFragment : Fragment() {
    var show = false
    private var _binding: FragmentHomeBinding? = null
    private var uvBar = 50

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
        return root
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
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(activity: FragmentActivity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(
            activity.currentFocus!!.windowToken,
            InputMethodManager.SHOW_FORCED,
            0
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}
