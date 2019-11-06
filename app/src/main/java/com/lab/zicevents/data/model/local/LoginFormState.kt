package com.lab.zicevents.data.model.local

data class LoginFormState
    (val emailError: Int? = null,
     val passwordError: Int? = null,
     val isDataValid : Boolean = false)