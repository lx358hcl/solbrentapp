package com.example.in2000_team32.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.in2000_team32.api.MetDataSource
import com.example.in2000_team32.databinding.FragmentMapBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }


        // -- JUST TESTING!!! API TESTING
        val btn: Button = binding.mapTestBtn
        val mDataSource = MetDataSource()
        btn.setOnClickListener {
            runBlocking { // THIS MUST **NOT** be used in the final app!!
                launch {
                    mDataSource.fetchMetWeatherForecast()
                }
            }
        }
        // -- TESTING OVER --


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}