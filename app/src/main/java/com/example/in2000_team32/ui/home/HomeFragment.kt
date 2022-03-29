package com.example.in2000_team32.ui.home

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.in2000_team32.R
import com.example.in2000_team32.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    var show = false

    private var _binding: FragmentHomeBinding? = null
    private var uvBar = 20

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

        UVbar.setProgress(uvBar)


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
        binding.searchButton.setBackgroundResource(R.drawable.ic_baseline_close_24)
        binding.EditTextAddress.requestFocus()
        activity?.let { showKeyboard(it) }
    }

    fun hideSearch(){
        var searchDistance = resources.getDimensionPixelSize(R.dimen.searchDistance).toFloat()
        show = false
        binding.searchLayout1.animate().translationY(searchDistance)
        binding.searchButton.setBackgroundResource(R.drawable.ic_baseline_search_24)
        hideKeyboard()
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
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
}


