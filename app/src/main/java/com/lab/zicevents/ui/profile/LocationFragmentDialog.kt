package com.lab.zicevents.ui.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.lab.zicevents.R
import com.lab.zicevents.data.api.geocoding.GeocodingApi
import com.lab.zicevents.data.model.api.geocoding.Address
import com.lab.zicevents.utils.base.BaseFragmentDialog
import kotlinx.android.synthetic.main.fragment_location_dialog.*
import android.widget.Toast
import com.lab.zicevents.data.model.database.user.User

/**
 * Dialog [Fragment] subclass.
 * Change user location
 */

class LocationFragmentDialog(private val userId: String,
                             private val address: Address? = null,
                             private val viewModel: ProfileViewModel) : BaseFragmentDialog(){

    private var tempAddress: Address? = null
    private var lastSearch: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_dialog, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = dialog
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            setUpToolbar()
            updateUI(address?.formattedAddress)
            observeGeocodingResult()
            // click listener on search location btn
            location_dialog_search.setOnClickListener {
                getUserPosition()
            }
            // click listener on save button
            location_dialog_save.setOnClickListener {
                tempAddress?.let {
                    if (it.formattedAddress != address?.formattedAddress)
                        showValidationDialog(it.formattedAddress)
                    else
                        dismiss()
                }
            }
        }
    }

    /**
     * Configure toolbar
     */
    override fun setUpToolbar() {
        location_dialog_toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        location_dialog_toolbar.setNavigationOnClickListener {
            dialog?.dismiss()
        }
    }

    /**
     * Update location text view with current user formattedAddress
     * formattedAddress may be null if user doesn't register an formattedAddress
     * @param formattedAddress user formattedAddress as string
     */
    private fun updateUI(formattedAddress: String?) {
        formattedAddress?.let {
            location_dialog_text.setText(it)
            lastSearch = it // block address search for same address
        }
    }

    /**
     * Get addresses components async from non formatted address string
     * get location input text as address only if not null or blank
     */
    private fun getUserPosition(){
        when {
            location_dialog_text.text.isNullOrBlank() ->
                location_dialog_location_inputLayout.error =
                    getString(R.string.geocoding_empty_text)

            location_dialog_text.text.toString().length < 5 ->
                location_dialog_location_inputLayout.error =
                    getString(R.string.geocoding_enough_text)

            location_dialog_text.text.toString() == lastSearch -> {} // block search for same query between 2 search

            else -> {
                showProgress(true)
                viewModel.getGeolocationAddress(location_dialog_text.text.toString(),
                    GeocodingApi.API_KEY)
                lastSearch = location_dialog_text.text.toString() // save last search string
            }
        }
    }

    /**
     * Observe geocoding request result and
     * show result if ok or display error message
     */
    private fun observeGeocodingResult(){
        viewModel.geocodingResult.observe(this, Observer {
            when {
                it.data is Address ->
                    displayResult(it.data)
                it.data == null -> {
                    location_dialog_location_inputLayout.error =
                        getString(R.string.geocoding_address_no_result)
                    // no result set save button disable
                    location_dialog_save.isEnabled = false
                }
                it.error is Int ->
                    Toast.makeText(context,getText(it.error),Toast.LENGTH_LONG).show()
                else -> {}
            }
            showProgress(false)
        })
    }

    /**
     * Display address in textView,
     */
    private fun displayResult(address: Address){
        val isAddressValid = address.formattedAddress != null && address.geometry != null
        if (isAddressValid) {
            location_dialog_location_inputLayout.error = null
            location_dialog_text.setText(address.formattedAddress) // Display formatted address result
            lastSearch = location_dialog_text.text.toString() // save last search string
            tempAddress = address // save tempAddress
        }
        // enable disable save btn
        location_dialog_save.isEnabled = isAddressValid
    }

    /**
     * Update User Profile with new address object
     */
    private fun updateUserProfile(){
        tempAddress?.let {
            val map = HashMap<String, Address>()
            map[User.ADDRESS_FIELD] = it
            viewModel.updateUserProfile(userId,map)
            this.dismiss()
        }
    }

    /**
     * Display progressBar when perform http request
     * @param visible boolean for show/hide progressBar
     */
    private fun showProgress(visible: Boolean) {
        if (visible){
            location_dialog_progress.visibility = View.VISIBLE
            location_dialog_search.visibility = View.GONE
        } else {
            location_dialog_progress.visibility = View.GONE
            location_dialog_search.visibility = View.VISIBLE
        }
    }

    /**
     * Config and show validation AlertDialog
     */
    private fun showValidationDialog(message: String?) {
        message?.let {
            // config Dialog
            val builder = AlertDialog.Builder(context)
            builder.setTitle(getText(R.string.alert_valid_address_title))
                .setMessage(it)
                .setPositiveButton(getText(R.string.alert_valid_address_positive_btn)) { _,_ ->
                    updateUserProfile()
                }
                .setNegativeButton(getText(R.string.alert_valid_address_negative_btn))
                { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
            // Config Style dialog btn
            val dialog = builder.create()
            dialog.show()
            // Apply btn background color transparent
            val positiveBtn =
                dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            val negativeBtn =
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            val background =
                resources.getColor(android.R.color.transparent)
            // apply bnt style
            positiveBtn.setBackgroundColor(background)
            negativeBtn.setBackgroundColor(background)
        }
    }
}
