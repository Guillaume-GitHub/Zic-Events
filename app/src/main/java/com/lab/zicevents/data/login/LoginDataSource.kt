package com.lab.zicevents.data.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class LoginDataSource {

    private val auth = FirebaseAuth.getInstance()

    fun login(email: String, password: String): LiveData<Boolean> {

        val loginSuccessful = MutableLiveData<Boolean>()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInWithEmail:success")
                loginSuccessful.value = true

            } else {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "signInWithEmail:failure", task.exception)
                loginSuccessful.value = false
            }
        }

        return loginSuccessful
    }

}