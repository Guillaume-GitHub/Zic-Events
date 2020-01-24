package com.lab.zicevents.utils.base

import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

abstract class BaseFragmentDialog: DialogFragment() {

    abstract fun setUpToolbar()
    protected fun setUpFullScreen() {

    }
}