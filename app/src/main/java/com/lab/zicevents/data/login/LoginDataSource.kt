package com.lab.zicevents.data.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class LoginDataSource {

    // Firebase Auth instance
    private val auth = FirebaseAuth.getInstance()

    /**
     * Try to authenticate user with his email and password
     * @param email input email string
     * @param password input password string
     * @return LiveData<Boolean>
     */
    fun signInWithEmailAndPassword(email: String, password: String): LiveData<Boolean> {

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

    /**
     * Try to create user with his email and password
     * @param email input email string
     * @param password input password string
     * @return LiveData<Boolean>
     */
    fun createUserWithEmailAndPassword(email: String, password: String) : LiveData<Boolean>{

        val createSuccessful = MutableLiveData<Boolean>()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    createSuccessful.value = true
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    createSuccessful.value = true
                }
            }

        return createSuccessful
    }

}