package com.example.in2000_team32.ui.home

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.in2000_team32.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val root: View = binding.root


        val homeLayout = binding.homeLayout
        val searchButton = binding.searchButton
        val textHome: TextView = binding.textHome
        val textUV: TextView = binding.textViewUV
        val textKlokke: TextView = binding.textViewKlokke
        val textDate: TextView = binding.textViewDato
        val textLeft: TextView = binding.textViewLeft
        val textLeftMid: TextView = binding.textViewLeftMid
        val textRightMid: TextView = binding.textViewRightMid
        val textRight: TextView = binding.textViewRight


        searchButton.setOnClickListener{
            ObjectAnimator.ofFloat(homeLayout, "translationY", -100f).apply {
                duration = 2000
                start()
            }
        }


        homeViewModel.textHome.observe(viewLifecycleOwner) {
            textHome.text = it
        }

        homeViewModel.textUV.observe(viewLifecycleOwner) {
            textUV.text = it
        }

        homeViewModel.textDate.observe(viewLifecycleOwner) {
            textDate.text = it
        }

        homeViewModel.textKlokke.observe(viewLifecycleOwner) {
            textKlokke.text = it
        }

        homeViewModel.textLeft.observe(viewLifecycleOwner) {
            textLeft.text = it
        }

        homeViewModel.textLeftMid.observe(viewLifecycleOwner) {
            textLeftMid.text = it
        }

        homeViewModel.textRightMid.observe(viewLifecycleOwner) {
            textRightMid.text = it
        }

        homeViewModel.textRight.observe(viewLifecycleOwner) {
            textRight.text = it
        }
        return root

    }






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}