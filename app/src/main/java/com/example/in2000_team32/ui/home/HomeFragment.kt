package com.example.in2000_team32.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.in2000_team32.R
import com.example.in2000_team32.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    var show = false

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
        hideSearch()



        val searchButton = binding.searchButton
        val UVbar = binding.progressBar

        UVbar.setProgress(100)


        searchButton.setOnClickListener{
            if (show){
                hideSearch()
            } else {
                showSearch()
            }
        }


        /*homeViewModel.textHome.observe(viewLifecycleOwner) {
            textHome.text = it
        }
         */

        return root

    }


    fun showSearch(){
        var searchDistance = resources.getDimensionPixelSize(R.dimen.searchDistance).toFloat()
        show = true
        binding.searchLayout1.animate().translationY(0F)
    }

    fun hideSearch(){
        var searchDistance = resources.getDimensionPixelSize(R.dimen.searchDistance).toFloat()
        show = false
        binding.searchLayout1.animate().translationY(searchDistance)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


