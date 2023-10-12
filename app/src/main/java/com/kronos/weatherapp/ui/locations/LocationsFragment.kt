package com.kronos.weatherapp.ui.locations

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.kronos.core.adapters.SwipeToDelete
import com.kronos.core.extensions.binding.fragmentBinding
import com.kronos.core.extensions.isToday
import com.kronos.core.extensions.of
import com.kronos.core.util.LoadingDialog
import com.kronos.core.util.show
import com.kronos.domian.model.DailyForecast
import com.kronos.domian.model.Hour
import com.kronos.domian.model.UserCustomLocation
import com.kronos.domian.model.forecast.Forecast
import com.kronos.weatherapp.R
import com.kronos.weatherapp.databinding.FragmentLocationBinding
import com.kronos.weatherapp.ui.weather.IndicatorAdapter
import com.kronos.weatherapp.ui.weather.WeatherDayAdapter
import com.kronos.weatherapp.ui.weather.WeatherHourAdapter
import com.kronos.weatherapp.ui.weather.model.Indicator
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import java.util.*

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
        initViewModel()
        initViews()
        setListeners()
        observeViewModel()
        root
    }

    private fun setListeners() {
    }

    private fun observeViewModel() {
        viewModel.locations.observe(this.viewLifecycleOwner, ::handleLocations)
        viewModel.loading.observe(this.viewLifecycleOwner, ::handleLoading)
        viewModel.error.observe(this.viewLifecycleOwner, ::handleError)
    }


    private fun handleError(hashtable: Hashtable<String, String>) {
        if (hashtable["error"] != null) {
            if (hashtable["error"]!!.isNotEmpty()) {
                show(
                    binding.recyclerViewLocations,
                    hashtable["error"].orEmpty(),
                    R.color.white,
                    R.color.primary_dark
                )
            } else {
                show(
                    binding.recyclerViewLocations,
                    hashtable["error"].orEmpty(),
                    R.color.white,
                    R.color.primary_dark
                )
            }
        }
    }

    private fun handleLoading(b: Boolean) {
        try {
            if (b) {
                LoadingDialog.getProgressDialog(
                    requireContext(),
                    R.string.loading_dialog_text,
                    R.color.primary_dark
                )!!.show()
            } else {
                LoadingDialog.getProgressDialog(
                    requireContext(),
                    R.string.loading_dialog_text,
                    R.color.primary_dark
                )!!.dismiss()
            }
        }catch (e:IllegalArgumentException){
            e.printStackTrace()
        }

    }

    private fun handleLocations(list: List<UserCustomLocation>?) {
        viewModel.userLocationAdapter.get()?.submitList(list)
        viewModel.userLocationAdapter.get()?.notifyDataSetChanged()
    }

    private fun initViews() {
        binding.recyclerViewLocations.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewLocations.setHasFixedSize(false)
        if (viewModel.userLocationAdapter.get() == null)
            viewModel.userLocationAdapter = WeakReference(UserLocationAdapter())
        binding.recyclerViewLocations.adapter = viewModel.userLocationAdapter.get()

        val itemTouchHelperCallback: ItemTouchHelper.Callback = object :
            ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        viewModel.deleteLocation(
                            viewModel.userLocationAdapter.get()!!.getItemAt(viewHolder.adapterPosition)
                        )
                    }
                    ItemTouchHelper.RIGHT -> {
                        viewModel.deleteLocation(
                            viewModel.userLocationAdapter.get()!!.getItemAt(viewHolder.adapterPosition)
                        )
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(
            SwipeToDelete(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_delete
                )!!,
                ColorDrawable(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.delete_color
                    )
                ),
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_delete
                )!!,
                ColorDrawable(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.delete_color
                    )
                ),
                itemTouchHelperCallback,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewLocations)

    }

    private fun initViewModel() {
        viewModel.getLocations()
    }

    override fun onDestroyView() {
        viewModel.destroy()
        binding.unbind()
        super.onDestroyView()
    }

    override fun onPause() {
        binding.unbind()
        super.onPause()
    }

}