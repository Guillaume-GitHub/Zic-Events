package com.lab.zicevents.data.api.maps

import com.lab.zicevents.BuildConfig
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock

class StaticMapTest {
    private var longitude = 0.0
    private var latitude = 0.0
    private var marker = true
    private var zoom = 0
    private var key = ""

    @Before
    fun setUp() {
        latitude = 47.125454
        longitude = 6.1254
        zoom = 10
        key = BuildConfig.GOOGLE_KEY
    }

    @Test
    fun staticMapUri() {
       val uriExpected =
            "https://maps.googleapis.com/maps/api/staticmap?size=200x200" +
                    "&center=$latitude,$longitude&scale=2&zoom=$zoom" +
                    "&markers=$latitude,$longitude&key=$key"

        assertEquals(StaticMap.getUrlStaticMap(longitude, latitude, marker, zoom), uriExpected)
    }

    @Test
    fun staticMapUriWithSize() {
        val width = 450
        val height = 250

        val uri =
            "https://maps.googleapis.com/maps/api/staticmap?size=${width}x${height}" +
                    "&center=$latitude,$longitude&scale=2&zoom=$zoom" +
                    "&markers=$latitude,$longitude&key=$key"

        assertEquals(StaticMap.getUrlStaticMap(longitude, latitude, marker, zoom), uri)
    }

}
