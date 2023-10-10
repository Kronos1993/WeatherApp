package com.kronos.core.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kronos.core.extensions.asLiveData
import java.util.*

open class ParentViewModel(
    var loading: MutableLiveData<Boolean> = MutableLiveData(),
    var error: MutableLiveData<Hashtable<String, String>> = MutableLiveData()
) : ViewModel() {

    init {
        loading.value = false
        error.value = Hashtable()
    }

    private val _recyclerLastPos = MutableLiveData<Int>()
    val recyclerLastPos = _recyclerLastPos.asLiveData()

    fun setRecyclerLastPosition(i: Int) {
        _recyclerLastPos.value = i
    }
}