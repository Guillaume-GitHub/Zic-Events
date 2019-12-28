package com.lab.zicevents.data.service

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import com.lab.zicevents.R
import java.io.IOException
import java.util.*

class FetchAddressIntentService : IntentService(Constants.SERVICE_NAME) {

    private val TAG = this::class.java.simpleName
    private var receiver: ResultReceiver? = null

    object Constants {
        const val SUCCESS_RESULT = 0
        const val FAILURE_RESULT = 1
        const val SERVICE_NAME = "FetchAddressIntentService"
        const val PACKAGE_NAME = "com.lab.zicevents"
        const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
        const val RESULT_DATA_KEY = "${PACKAGE_NAME}.RESULT_DATA_KEY"
        const val LOCATION_DATA_EXTRA = "${PACKAGE_NAME}.LOCATION_DATA_EXTRA"
    }


    override fun onHandleIntent(intent: Intent?) {
        intent ?: return

        var errorMessage = ""
        val geocoder = Geocoder(this, Locale.FRANCE)

        receiver = intent.getParcelableExtra(Constants.RECEIVER)
        // Get the location passed to this service through an extra.
        val location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA) as Location

        var addresses: List<Address> = emptyList()

        try {
            addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1)// Number of max result. Here get just a single address.
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available)
            Log.e(TAG, errorMessage, ioException)

        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used)
            Log.e(TAG, "$errorMessage. Latitude = $location.latitude , " +
                    "Longitude =  $location.longitude", illegalArgumentException)
        }

        // Handle case where no address was found.
        if (addresses.isEmpty()) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found)
                Log.e(TAG, errorMessage)
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage)

        } else {
            val address = addresses[0]
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            val addressFragments = with(address) {
                (0..maxAddressLineIndex).map { getAddressLine(it) }
            }
            Log.i(TAG, getString(R.string.address_found))
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                addressFragments.joinToString(separator = "\n"))
        }

    }

    /**
     * Send result to Receiver Class
     * @param resultCode is status code of job
     * @param message is information message of job
     *
     */
    private fun deliverResultToReceiver(resultCode: Int, message: String) {
        val bundle = Bundle().apply { putString(Constants.RESULT_DATA_KEY, message) }
        receiver?.send(resultCode, bundle)
    }

}