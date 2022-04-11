package com.example.in2000_team32.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.in2000_team32.R
import com.example.in2000_team32.databinding.FragmentProfileBinding
import dev.sasikanth.colorsheet.ColorSheet



class ProfileFragment : Fragment() {

    companion object {
        private const val COLOR_SELECTED = "selectedColor"
        private const val NO_COLOR_OPTION = "noColorOption"
    }

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var selectedColor: Int = ColorSheet.NO_COLOR
    private var noColorOption = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val colors = resources.getIntArray(R.array.colors)


        binding.editSkin.setOnClickListener {
            ColorSheet().cornerRadius(8)
                .colorPicker(
                    colors = colors,
                    noColorOption = noColorOption,
                    selectedColor = selectedColor,
                    listener = { color ->
                        selectedColor = color
                        setColor(selectedColor)
                    })
                .show(getParentFragmentManager())
        }



        val profileText: TextView = binding.minProfilText
        dashboardViewModel.profileText.observe(viewLifecycleOwner) {
            profileText.text = it
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}