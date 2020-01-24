package com.lab.zicevents.ui.profile


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.utils.base.BaseFragmentDialog
import kotlinx.android.synthetic.main.fragment_description_dialog.*

/**
 * Dialog [Fragment] subclass.
 * Change user description
 */
class DescriptionFragmentDialog(
    private val userId: String,
    private val description: String? = null,
    private val viewModel: ProfileViewModel): BaseFragmentDialog() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_description_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = dialog
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            setUpToolbar()
            updateUI(description)

            // Observe Text change
            description_dialog_text.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(text: Editable?) {
                    if (text.toString().compareTo(description.toString()) != 0){
                        description_dialog_save.isEnabled = // Enable/Disable btn
                            viewModel.isDescriptionValid(text.toString()) // Verify if text is valid
                    } else
                        description_dialog_save.isEnabled = false
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                   //
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    //
                }
            })

            // Update User profile with new description
            description_dialog_save.setOnClickListener {
                val map = HashMap<String,String>()
                map[User.DESCRIPTION_FIELD] = description_dialog_text.text.toString()
                viewModel.updateUserProfile(userId, map)
                dialog.dismiss() // Close dialog
            }
        }
    }

    /**
     * Configure navigation toolbar
     */
    override fun setUpToolbar() {
        description_dialog_toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        description_dialog_toolbar.setNavigationOnClickListener {
            dialog.dismiss()
        } // cancel operation
    }

    /**
     * Display non empty description in textView
     */
    private fun updateUI(description: String?){
       description?.let {
           description_dialog_text.setText(it)
       }
    }
}
