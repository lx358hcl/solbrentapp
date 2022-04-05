package com.example.in2000_team32.ui.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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

    override fun onCreateView(inflater: LayoutInflater,  container: ViewGroup?, savedInstanceState: Bundle?): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val root: View = binding.root

        hideSearch()
        hideKeyboard()

        val searchButton = binding.searchButton
        val UVbar = binding.progressBar

        UVbar.setProgress(uvBar)

        searchButton.setOnClickListener{
            if (show) {
                hideSearch()
            } else {
                showSearch()
            }
        }

        binding.imageViewSolkrem.setImageResource(R.drawable.solkrem_lang_15)

        fun getGeoLocation(activity : Activity){
            var fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    44
                )
                //Her må du ha permission onResultChecker...
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        homeViewModel.fetchLocationData(location.latitude, location.longitude)
                        homeViewModel.fetchWeatherData(location.latitude, location.longitude)
                    }
                }
            }
            else {
                fusedLocationClient.getLastLocation()
                fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        homeViewModel.fetchLocationData(location.latitude, location.longitude)
                        homeViewModel.fetchWeatherData(location.latitude, location.longitude)
                    }

                    // Get UV data
                    getActivity()?.let {
                        homeViewModel.getUvData().observe(it) {
                            binding.textUvi.setText(it.toString())
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
            }
        }

        // Get data
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
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(activity: FragmentActivity) {
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

}