package com.lab.zicevents.data.api.maps

import android.util.Log
import com.lab.zicevents.BuildConfig

class StaticMap {

    companion object {

        private const val BASE_URL = "https://maps.googleapis.com/maps/api/staticmap"
        private const val DEFAULT_SIZE = "200x200"
        private const val DEFAULT_SCALE = 2

        /**
         * Return Url of map image corresponding to params
         */
        fun getUrlStaticMap(
            longitude: Double,
            latitude: Double,
            marker: Boolean,
            zoom: Int,
            width: Int? = null,
            height: Int? = null
        ): String {

            val params: HashMap<String, String> = HashMap()

            params["center"] = "${latitude},${longitude}"
            params["zoom"] = zoom.toString()

            if (marker)
                params["markers"] = "${latitude},${longitude}"

            val size =
                if (width != null && height != null)
                    "${width / DEFAULT_SCALE}x${height / DEFAULT_SCALE}"
                else DEFAULT_SIZE

            params["size"] = size
            params["scale"] = "2"
            params["key"] = BuildConfig.GOOGLE_KEY

            val url =
                "$BASE_URL?${params.map { entry -> "${entry.key}=${entry.value}" }
                    .reduce { lastEntry, currentEntry -> "${lastEntry}&${currentEntry}" }
                }"
            Log.d(this::class.java.simpleName, "StaticMap url --> $url")

            return url
        }
    }
}