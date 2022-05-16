package com.example.in2000_team32.ui.profile

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.in2000_team32.R
import com.example.in2000_team32.api.DataSourceRepository
import com.example.in2000_team32.api.DataSourceSharedPreferences
import com.example.in2000_team32.databinding.FragmentProfileBinding
import com.example.in2000_team32.ui.home.HomeViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import dev.sasikanth.colorsheet.ColorSheet


class ProfileFragment : Fragment() {

    companion object {
        private const val COLOR_SELECTED = "selectedColor"
        private const val NO_COLOR_OPTION = "noColorOption"
    }

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var selectedColor: Int = ColorSheet.NO_COLOR
    private var noColorOption = false

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var sharedPreferences: DataSourceSharedPreferences
    private lateinit var dataSourceRepository: DataSourceRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val colors = resources.getIntArray(R.array.colors)
        sharedPreferences = DataSourceSharedPreferences(requireContext())
        dataSourceRepository = DataSourceRepository(requireContext())

        //Buttons from settinsg page
        var darkModeButtonTextLower : TextView = root.findViewById(R.id.darkModeButtonTextLower)
        var darkModeButton : SwitchMaterial = root.findViewById(R.id.darkModeButton)
        val varslerButtonTextLower : TextView = root.findViewById(R.id.VarslerButtonTextLower)
        val varslerButton : SwitchMaterial = root.findViewById(R.id.VarslerButton)
        val unitButton : SwitchMaterial = root.findViewById(R.id.unitSettingsButton)
        val unitText : TextView = root.findViewById(R.id.unitSettingsText)


        //Check if sharedPreferences has a value for darkMode
        if(sharedPreferences.getThemeMode() == null){
            sharedPreferences.setThemeMode("light")
        }
        //Update buttons based on sharedPreferences data
        if (sharedPreferences.getThemeMode() == "dark"){
            darkModeButtonTextLower.text = "Mørk"
            //Uncheck the switchMaterial button
            darkModeButton.isChecked = true
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        }
        else {
            darkModeButtonTextLower.text = "Lys"
            //Set app theme to light mode li
            //Check the button
            darkModeButton.isChecked = false
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }
        if (dataSourceRepository.getNotifPref()) {
            varslerButtonTextLower.text = "På"
            varslerButton.isChecked = true
        }
        else {
            varslerButtonTextLower.text = "Av"
            varslerButton.isChecked = false
        }

        // Set temp unit based on shared preferences
        val currentUnit = sharedPreferences.getTempUnit()
        unitButton.isChecked = currentUnit
        if (currentUnit) unitText.text = "Celsius" else unitText.text = "Farhenheit"

        //Get fitz from sharedPreferences and set background color
        binding.constraintLayout1.setBackgroundColor(homeViewModel.getColor())

        //Listen for click on dark mode button and change theme
        binding.darkModeButton.setOnClickListener {
            if (sharedPreferences.getThemeMode() == "light") {
                sharedPreferences.setThemeMode("dark")
                darkModeButtonTextLower.text = "Mørk"
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            }
            else {
                sharedPreferences.setThemeMode("light")
                darkModeButtonTextLower.text = "Lys"
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            }
        }

        //Listen for click on dark mode button and change theme
        binding.VarslerButton.setOnClickListener {
            if (dataSourceRepository.getNotifPref()) {
                dataSourceRepository.setNotifPref(false)
                varslerButtonTextLower.text = "Av"
            }
            else {
                dataSourceRepository.setNotifPref(true)
                varslerButtonTextLower.text = "På"
            }
        }

        // Change temperature units
        binding.unitSettingsButton.setOnClickListener {
            sharedPreferences.toggleTempUnit()
            val currentUnit : Boolean = sharedPreferences.getTempUnit()
            if (currentUnit) {
                unitText.text = getString(R.string.celsius)
            } else {
                unitText.text = getString(R.string.fahrenheit)
            }
        }

        binding.editSkin.setOnClickListener {
            ColorSheet().cornerRadius(8)
                .colorPicker(
                    colors = colors,
                    noColorOption = noColorOption,
                    selectedColor = selectedColor,
                    listener = { color ->
                        selectedColor = color
                        setColor(selectedColor)
                        homeViewModel.writeColor(selectedColor) //Skriver til sharedPreferences den valgte hudfargen

                        // Mapping av color til fitz (burde egentlig vært gjort motsatt, men det er litt jobb å fikse)
                        var fitzType : Int
                        when (selectedColor) {
                            -798540 -> fitzType = 1
                            -1657709 -> fitzType = 2
                            -2842236 -> fitzType = 3
                            -2980001 -> fitzType = 4
                            -6070719 -> fitzType = 5
                            -12902628 -> fitzType = 6
                            else -> fitzType = 0
                        }
                        dataSourceRepository.writeFitzType(fitzType)
                    })
                .show(getParentFragmentManager())
        }

        val profileText: TextView = binding.minProfilText
        dashboardViewModel.profileText.observe(viewLifecycleOwner) {
            profileText.text = it
        }

        //Sjekker om sharedPreferences har lagret en farge fra tidligere. Endrer til denne fargen
        val farge = homeViewModel.getColor()
        if(farge != 0) {
            binding.constraintLayout1.setBackgroundColor(homeViewModel.getColor())
        } else {
            var background = ResourcesCompat.getDrawable(this.resources, R.drawable.bg_gradient, null) as GradientDrawable
            binding.constraintLayout1.setBackgroundDrawable(background)
        }

        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(COLOR_SELECTED, selectedColor)
        outState.putBoolean(NO_COLOR_OPTION, noColorOption)
    }

    private fun setColor(@ColorInt color: Int) {
        if (color != ColorSheet.NO_COLOR) {
            binding.constraintLayout1.setBackgroundColor(color)
        } else {
            binding.constraintLayout1.setBackgroundColor(R.drawable.bg_gradient)
        }
    }


    //Function to change theme to dark mode or light mode when the user clicks on the button
    fun changeTheme(view: View) {
        val currentTheme = sharedPreferences.getThemeMode()
        if (currentTheme == "light") {
            sharedPreferences.setThemeMode("dark")
            activity?.recreate()
        }
        else {
            sharedPreferences.setThemeMode("light")
            activity?.recreate()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}