package com.lab.zicevents.utils

import androidx.test.platform.app.InstrumentationRegistry
import com.lab.zicevents.utils.adapter.NetworkConnectivity

import org.junit.Assert.*
import org.junit.Test

class NetworkConnectivityTest {

    @Test
    fun when_use_default_host_and_internet_available_return_true() {
        assertEquals(true, NetworkConnectivity.isConnected())
    }

    @Test
    fun when_use_correct_host_and_internet_available_return_true() {
        // Set valid Host name
        val host = "google-public-dns-a.google.com"
        assertEquals(true, NetworkConnectivity.isConnected(host))
    }

    @Test
    fun when_use_invalid_host_and_internet_available_return_false() {
        // Set valid Host name
        val host = "test-wrong-host#"
        assertEquals(false, NetworkConnectivity.isConnected(host))
    }

    @Test
    fun checkIsOnlineTrue() {
        // Set valid Host name
        val context = InstrumentationRegistry.getInstrumentation().context
        assertEquals(true, NetworkConnectivity.isOnline(context))
    }
}