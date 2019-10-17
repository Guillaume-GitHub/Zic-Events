package com.lab.zicevents.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lab.zicevents.data.login.LoginRepository

class LoginViewModel(private val loginRepository: LoginRepository): ViewModel() {

   fun login(email: String, password: String): LiveData< Boolean> {
     return  loginRepository.login(email, password)
   }
}