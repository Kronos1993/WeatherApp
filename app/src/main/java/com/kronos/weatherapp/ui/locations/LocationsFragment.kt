package com.kronos.weatherapp.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kronos.core.extensions.binding.fragmentBinding
import com.kronos.weatherapp.R
import com.kronos.weatherapp.databinding.FragmentLocationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationsFragment : Fragment() {

    private val binding by fragmentBinding<FragmentLocationBinding>(R.layout.fragment_location)

    private val viewModel by viewModels<LocationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.run {
        lifecycleOwner = this@LocationsFragment.viewLifecycleOwner
        setHasOptionsMenu(true)
        root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }
}