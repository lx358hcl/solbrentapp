package com.example.in2000_team32.ui.map


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.in2000_team32.R
import com.example.in2000_team32.databinding.FragmentMapBinding

import android.widget.TextView




class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var tidGår = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Viser og gjemmer brent seg tips
        //Viser
        binding.brentSegShow.setOnClickListener() {
            // previously invisible view
            val myView: View = binding.brentSegTips

            // Check if the runtime version is at least Lollipop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // get the center for the clipping circle
                val cx = myView.width / 2
                val cy = myView.height / 2

                // get the final radius for the clipping circle
                val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

                // create the animator for this view (the start radius is zero)
                val anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, finalRadius)
                // make the view visible and start the animation
                myView.visibility = View.VISIBLE
                anim.start()
            } else {
                // set the view to invisible without a circular reveal animation below Lollipop
                myView.visibility = View.GONE
            }

        }
        //Gjemmer
        binding.brentSegHide.setOnClickListener() {
            // previously visible view
            val myView: View = binding.brentSegTips

            // Check if the runtime version is at least Lollipop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // get the center for the clipping circle
                val cx = myView.width / 2
                val cy = myView.height / 2

                // get the initial radius for the clipping circle
                val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

                // create the animation (the final radius is zero)
                val anim =
                    ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0f)

                // make the view invisible when the animation is done
                anim.addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        myView.visibility = View.GONE
                    }
                })

                // start the animation
                anim.start()
            } else {
                // set the view to visible without a circular reveal animation below Lollipop
                myView.visibility = View.VISIBLE
            }

        }

        val spinner: Spinner = binding.spfSpinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spfSpinnerValues,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        /*
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerSelected = spinner.getSelectedItem().toString()
                TODO("Not yet implemented")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

         */

        binding.smurtSegButton.setOnClickListener{
            timer()
        }
        binding.smurtSegAvbryt.setOnClickListener{
            stoppTimer()
        }

        return root
    }

    fun timer(){
        binding.smurtSegButton.visibility = View.GONE
        binding.spfSpinner.visibility = View.GONE
        binding.smurtSegAvbryt.visibility = View.VISIBLE
        binding.smurtSegIgjen.visibility = View.VISIBLE
    }

    fun stoppTimer(){
        binding.smurtSegButton.visibility = View.VISIBLE
        binding.spfSpinner.visibility = View.VISIBLE
        binding.smurtSegAvbryt.visibility = View.GONE
        binding.smurtSegIgjen.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

