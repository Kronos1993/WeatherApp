package com.kronos.weatherapp.ui.locations.add

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kronos.core.extensions.binding.fragmentBinding
import com.kronos.domian.model.UserCustomLocation
import com.kronos.weatherapp.R
import com.kronos.weatherapp.databinding.FragmentAddLocationDialogBinding
import com.kronos.weatherapp.ui.locations.LocationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddLocationDialogFragment : BottomSheetDialogFragment() {

    private val binding by fragmentBinding<FragmentAddLocationDialogBinding>(R.layout.fragment_add_location_dialog)

    private val locationViewModel by activityViewModels<LocationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.run {
        viewModel = this@AddLocationDialogFragment.locationViewModel
        lifecycleOwner = this@AddLocationDialogFragment.viewLifecycleOwner
        setListeners()
        root
    }

    private fun setListeners() {
        binding.buttonAddParcel.setOnClickListener {
            if (locationViewModel.validateField()){
                hideDialog()
                locationViewModel.addLocation()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpDialog()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    private fun setUpDialog() {
        this.isCancelable = false
        binding.imageView.setOnClickListener {
            locationViewModel.cityName.set(null)
            hideDialog()
        }
    }

    private fun hideDialog() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                findNavController().navigate(R.id.action_navigation_add_location_to_navigation_location)
            }
        }
    }

    override fun onDestroy() {
        binding.unbind()
        super.onDestroy()
    }

}