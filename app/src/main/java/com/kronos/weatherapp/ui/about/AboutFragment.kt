package com.kronos.weatherapp.ui.about


import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kronos.core.extensions.binding.fragmentBinding
import com.kronos.weatherapp.R
import com.kronos.weatherapp.databinding.FragmentAboutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : Fragment() {

    private val binding by fragmentBinding<FragmentAboutBinding>(R.layout.fragment_about)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.run {
        lifecycleOwner = this@AboutFragment.viewLifecycleOwner
        setHasOptionsMenu(true)
        root
    }

    override fun onDestroyView() {
        binding.unbind()
        super.onDestroyView()
    }

    override fun onPause() {
        binding.unbind()
        super.onPause()
    }

}