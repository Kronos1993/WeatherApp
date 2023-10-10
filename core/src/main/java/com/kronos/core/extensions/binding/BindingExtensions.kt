package com.kronos.core.extensions.binding

import android.app.Activity
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class BindingProperty<T, B : ViewDataBinding> : ReadOnlyProperty<T, B> {

    private var binding: B? = null
    override fun getValue(thisRef: T, property: KProperty<*>): B =
        binding ?: createBinding(thisRef).also { binding = it }

    abstract fun createBinding(thisRef: T): B
}

fun <T : ViewDataBinding> activityBinding(@LayoutRes resId: Int): BindingProperty<Activity, T> =
    object : BindingProperty<Activity, T>() {
        override fun createBinding(thisRef: Activity): T =
            DataBindingUtil.setContentView(thisRef, resId)
    }

fun <T : ViewDataBinding> fragmentBinding(@LayoutRes resId: Int): ReadOnlyProperty<Fragment, T> =
    object : BindingProperty<Fragment, T>() {
        override fun createBinding(thisRef: Fragment): T =
            DataBindingUtil.inflate(LayoutInflater.from(thisRef.context), resId, null, false)
    }

fun <T : ViewDataBinding> bottomSheetDialogFragmentBinding(@LayoutRes resId: Int): ReadOnlyProperty<BottomSheetDialogFragment, T> =
    object : BindingProperty<BottomSheetDialogFragment, T>() {
        override fun createBinding(thisRef: BottomSheetDialogFragment): T =
            DataBindingUtil.inflate(LayoutInflater.from(thisRef.context), resId, null, false)
    }

fun <T : ViewDataBinding> dialogFragmentBinding(@LayoutRes resId: Int): ReadOnlyProperty<DialogFragment, T> =
    object : BindingProperty<DialogFragment, T>() {
        override fun createBinding(thisRef: DialogFragment): T =
            DataBindingUtil.inflate(LayoutInflater.from(thisRef.context), resId, null, false)
    }