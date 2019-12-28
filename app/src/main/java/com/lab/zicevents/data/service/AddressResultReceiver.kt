package com.lab.zicevents.data.service

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log

class AddressResultReceiver(handler: Handler): ResultReceiver(handler) {

    private lateinit var addressOutput: String

    /**
     * Receive result from ResultReceiver
     * @param resultCode is status code of operation
     * @param resultData containing data of operation, may be null
     */
    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {

        // Display the address string
        // or an error message sent from the intent service.
        addressOutput = resultData?.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY) ?: ""
        Log.d("Address ", addressOutput)

        // Log message if an address was found.
        if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
            Log.d("RESULT SUCCESS : ", "Address Found")
        }
    }

}