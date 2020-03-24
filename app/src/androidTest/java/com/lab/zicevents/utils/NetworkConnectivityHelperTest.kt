package com.lab.zicevents.utils

import com.lab.zicevents.utils.base.NetworkConnectivityHelper
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class NetworkConnectivityHelperTest {

    @Test
    fun when_use_default_host_and_internet_available_return_true() {
        assertEquals(true, NetworkConnectivityHelper.isInternetAvailable())
    }

    @Test
    fun when_use_correct_host_and_internet_available_return_true() {
        // Set valid Host name
        val host = "google-public-dns-a.google.com"
        assertEquals(true, NetworkConnectivityHelper.isInternetAvailable(host))
    }

    @Test
    fun when_use_invalid_host_and_internet_available_return_false() {
        // Set valid Host name
        val host = "test-wrong-host#"
        assertEquals(false, NetworkConnectivityHelper.isInternetAvailable(host))
    }

}