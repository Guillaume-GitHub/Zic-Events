package com.lab.zicevents.ui.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.material.chip.Chip

import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.utils.base.BaseFragmentDialog
import kotlinx.android.synthetic.main.fragment_style_dialog.*

/**
 * A simple [Fragment] subclass.
 */
class StyleFragmentDialog(private val userId: String,
                          private var viewModel: ProfileViewModel,
                          private var styleList: ArrayList<String>? = null)
    : BaseFragmentDialog(), View.OnClickListener {

    private var tempStyleList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_style_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = dialog

        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            setUpToolbar()
            updateUI(styleList)
            // Set Up dropdown adapter
            style_dialog_autocomplete.setAdapter(ArrayAdapter<String>(context!!,
                R.layout.support_simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.music_gender)
                )
            )
            // Show drop down list when view is focused
            style_dialog_autocomplete.setOnFocusChangeListener {_, isFocused ->
                if (isFocused) style_dialog_autocomplete.showDropDown()
            }
            // Show drop down list when view is clicked
            style_dialog_autocomplete.setOnClickListener {
                style_dialog_autocomplete.showDropDown()
            }
            // Add style when item in dropdown list is clicked
            style_dialog_autocomplete.setOnItemClickListener {_, view, _, _ ->
                addStyle((view as TextView).text.toString()) // add New chip
            }
            // Add style when + button is clicked
            style_dialog_add_btn.setOnClickListener {
                if (!style_dialog_autocomplete.text.isNullOrBlank())
                    addStyle(viewModel.formattedMusicStyle(
                        style_dialog_autocomplete.text.toString())
                    )
            }
            // Update profile when save button is clicked
            style_dialog_save.setOnClickListener {
                updateProfile()
            }
        }
    }

    /**
     * Configure navigation toolbar
     */
    override fun setUpToolbar() {
        style_dialog_toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        style_dialog_toolbar.setNavigationOnClickListener {
            dialog?.dismiss()
        } // cancel operation
    }
    /**
     * Display non empty description in textView
     */
    private fun updateUI(styleList: ArrayList<String>?){
        styleList?.let {
            // Set value to actual list
            tempStyleList.addAll(styleList)
            // Add Chip in group foreach element
            styleList.forEach {
                style_dialog_chipGroup.addView(
                    viewModel.getFormattedChip(
                        context!!, it, true, this)
                )
            }
        }
    }

    /**
     * Adding new chip in chip group
     * @param style music style as text
     */
    private fun addStyle(style: String){
        // clean autocomplete text
        style_dialog_autocomplete.text = null
        if (!tempStyleList.contains(style)){
            // add to temp list
            tempStyleList.add(style)
            // Create chip
            style_dialog_chipGroup.addView(viewModel.getFormattedChip(
                context!!, style, true,this)
            )
        }
    }

    /**
     * remove chip in chip group and tempList
     * @param chip you want to remove
     */
    private fun removeStyle(chip: Chip){
        tempStyleList.remove(chip.text.toString())
        style_dialog_chipGroup.removeView(chip)
    }

    /**
     * Update user profile with new music style list
     * update with null if temp list is empty
     * Dismiss dialog
     */
    private fun updateProfile(){
        if (tempStyleList != styleList) {
            val map = HashMap<String, ArrayList<String>?>()
            map[User.MUSIC_STYLE_FIELD] = if (tempStyleList.isEmpty()) null else tempStyleList
            viewModel.updateUserProfile(userId, map)
        }
        dialog.dismiss()
    }

    override fun onClick(view: View?) {
        when (view) {
            is Chip -> removeStyle(view)
            else -> {}
        }
    }
}
