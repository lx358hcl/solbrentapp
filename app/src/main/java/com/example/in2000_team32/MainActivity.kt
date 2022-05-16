package com.example.in2000_team32

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.in2000_team32.api.DataSourceSharedPreferences
import com.example.in2000_team32.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

lateinit var contextOfApplication : Context


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: androidx.navigation.NavController
    var internetLost = false

    fun isNetworkAvailable(): Boolean {
        var result = false
        val cm = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        } else {
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
        return result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataSourceSharedPreferences = DataSourceSharedPreferences(this)
        getSupportActionBar()?.hide()
        contextOfApplication = getApplicationContext()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_profile, R.id.navigation_map
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val progressBar = binding.progressBar3
        progressBar.visibility = View.GONE
        //Get fragment with id home_layout

        //Update the theme based on the shared preferences value
        if(dataSourceSharedPreferences.getThemeMode() == "dark") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        //Listen for internet connection established or lost and print message
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                runOnUiThread {
                    //Internet is back bitch
                    if(internetLost) {
                        Toast.makeText(this@MainActivity, "Internet is back", Toast.LENGTH_SHORT).show()
                        internetLost = false
                        //Restart activity without transition animation to avoid weird animation when coming back from settings activity to main activity
                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        startActivity(intent)
                    }
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                internetLost = true
                runOnUiThread {
                    //If current fragment is home fragment
                    if (navController.currentDestination?.id == R.id.navigation_home) {
                        progressBar.visibility = View.VISIBLE
                        //Make the home fragment visibiity VISIBLE
                        navController.navigate(R.id.navigation_home)

                    }
                }
            }
        }
        //Listens for internet connection established
        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        //Listen for fragment changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(!isNetworkAvailable()) {
                when (destination.id) {
                    R.id.navigation_home -> {
                        //Check if internet is
                        progressBar.visibility = View.VISIBLE
                    }
                    R.id.navigation_profile -> {
                        progressBar.visibility = View.GONE
                    }
                    R.id.navigation_map -> {
                        progressBar.visibility = View.GONE
                    }
                }
            }
            else{
                progressBar.visibility = View.GONE
            }
        }
    }
}