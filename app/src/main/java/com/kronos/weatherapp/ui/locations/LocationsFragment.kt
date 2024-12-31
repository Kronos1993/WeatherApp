package com.kronos.weatherapp.ui.locations

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.kronos.core.adapters.AdapterItemClickListener
import com.kronos.core.adapters.SwipeToDelete
import com.kronos.core.extensions.binding.fragmentBinding
import com.kronos.core.util.PreferencesUtil
import com.kronos.core.util.getProgressDialog
import com.kronos.core.util.setLanguageForApp
import com.kronos.core.util.show
import com.kronos.domian.model.UserCustomLocation
import com.kronos.weatherapp.R
import com.kronos.weatherapp.databinding.FragmentLocationBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import java.util.Date
import java.util.Hashtable

@AndroidEntryPoint
class LocationsFragment : Fragment() {

    private val binding by fragmentBinding<FragmentLocationBinding>(R.layout.fragment_location)

    private val viewModel by activityViewModels<LocationViewModel>()
    private var progressDialog: SweetAlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.run {
        setLanguageForApp(
            requireContext(),
            PreferencesUtil.getPreference(
                requireContext(),
                requireContext().getString(R.string.default_lang_key),
                requireContext().getString(R.string.default_language_value)
            )!!
        )
        lifecycleOwner = this@LocationsFragment.viewLifecycleOwner
        viewModel = this@LocationsFragment.viewModel
        progressDialog = getProgressDialog(
            requireContext(),
            com.kronos.resources.R.string.loading_dialog_text,
            com.kronos.resources.R.color.primary
        )
        root
    }

    override fun onResume() {
        super.onResume()
        initViewModel()
        initViews()
        setListeners()
        observeViewModel()
    }

    private fun setListeners() {
        binding.addLocations.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_location_to_navigation_add_location)
        }
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
                    com.kronos.resources.R.color.white,
                    com.kronos.resources.R.color.primary_dark
                )
            } else {
                show(
                    binding.recyclerViewLocations,
                    hashtable["error"].orEmpty(),
                    com.kronos.resources.R.color.white,
                    com.kronos.resources.R.color.primary_dark
                )
            }
        }
    }

    private fun handleLoading(b: Boolean) {
        try {
            if (b) {
                progressDialog?.show()
            } else {
                progressDialog?.dismiss()
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

    }

    private fun handleLocations(list: List<UserCustomLocation>?) {
        viewModel.userLocationAdapter.get()?.submitList(list)
        viewModel.userLocationAdapter.get()
            ?.notifyItemRangeChanged(0, viewModel.userLocationAdapter.get()!!.itemCount)
    }

    private fun initViews() {
        binding.recyclerViewLocations.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerViewLocations.setHasFixedSize(false)
        if (viewModel.userLocationAdapter.get() == null)
            viewModel.userLocationAdapter = WeakReference(UserLocationAdapter())
        binding.recyclerViewLocations.adapter = viewModel.userLocationAdapter.get()
        viewModel.userLocationAdapter.get()?.setUrlProvider(viewModel.urlProvider)
        viewModel.userLocationAdapter.get()?.setAdapterItemClick(object :
            AdapterItemClickListener<UserCustomLocation> {
            override fun onItemClick(t: UserCustomLocation, pos: Int) {
                viewModel.setLocationSelected(t)
            }

        })

        val itemTouchHelperCallback: ItemTouchHelper.Callback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.deleteLocation(
                    viewModel.userLocationAdapter.get()!!.getItemAt(viewHolder.adapterPosition)
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(
            SwipeToDelete(
                ContextCompat.getDrawable(
                    requireContext(),
                    com.kronos.resources.R.drawable.ic_delete
                )!!,
                ColorDrawable(
                    ContextCompat.getColor(
                        requireContext(),
                        com.kronos.resources.R.color.delete_color
                    )
                ),
                ContextCompat.getDrawable(
                    requireContext(),
                    com.kronos.resources.R.drawable.ic_delete
                )!!,
                ColorDrawable(
                    ContextCompat.getColor(
                        requireContext(),
                        com.kronos.resources.R.color.delete_color
                    )
                ),
                itemTouchHelperCallback,
                ItemTouchHelper.RIGHT
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewLocations)

    }

    private fun initViewModel() {
        viewModel.postDate(Date())
        viewModel.getLocations()
    }

    override fun onDestroyView() {
        binding.unbind()
        super.onDestroyView()
    }

    override fun onPause() {
        viewModel.destroy()
        super.onPause()
    }

}