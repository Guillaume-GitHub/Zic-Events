package com.lab.zicevents.data.api.geocoding

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GeocodingApiTest {
    private lateinit var geocodingUrl: String

    @Before
    fun init() {
        geocodingUrl = "https://maps.googleapis.com/"
    }

    @Test
    fun createRightInterface(){
        assertTrue(GeocodingApi.create() is GeocodingApiInterface)
    }

    @Test
    fun containRightUrl(){
        assertEquals(GeocodingApi.create(), geocodingUrl)
    }
}