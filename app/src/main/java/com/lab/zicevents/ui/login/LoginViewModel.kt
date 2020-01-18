package com.lab.zicevents.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import com.lab.zicevents.R
import com.lab.zicevents.data.login.LoginRepository
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.database.user.UserRepository
import com.lab.zicevents.data.model.database.user.PrivateUserInfo
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.data.model.local.*
import kotlinx.coroutines.*
import java.lang.ClassCastException
import java.util.*

class LoginViewModel(val loginRepository: LoginRepository,
                     private val userRepository: UserRepository
): ViewModel() {

    private val TAG = this::class.java.simpleName

    private val loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = loginForm

    private val loginUser = MutableLiveData<LoginUserState>()
    val loginUserState: LiveData<LoginUserState> = loginUser

    private val profileUser = MutableLiveData<InsertProfileState>()
    val insertProfileState: LiveData<InsertProfileState> = profileUser

    private val profileForm = MutableLiveData<ProfileFormState>()
    val profileFormState: LiveData<ProfileFormState> = profileForm

    private val data = MutableLiveData<DataResult>()
    val dataResult: LiveData<DataResult> = data

    private val resetPassword = MutableLiveData<Int>()
    val resetPasswordStatus: LiveData<Int> = resetPassword

    private val emailValid = MutableLiveData<Int?>()
    val emailValidState: LiveData<Int?> = emailValid

    /**
     * Launch Firebase sign in coroutine and get the authentication result as Result<AuthResult>
     * Pass Result to loginUserStateChanged method
     * @param credential AuthCredential
     */
    fun signInWithCredential(credential: AuthCredential) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = loginRepository.signInWithCredential(credential)
            loginUserStateChanged(result)
        }
    }

    /**
     * Launch new user creation coroutine and get the authentication result as Result<AuthResult>
     * Pass Result to loginUserStateChanged method
     * @param email String containing email address
     * @param password String containing password
     */
    fun createUserWithEmailAndPassword(email: String, password: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = loginRepository.createUserWithEmailAndPassword(email, password)
            loginUserStateChanged(result)
        }
    }

    /**
     * Launch send Password Reset Email coroutine and get the result state
     * @param email is email address
     */
    fun sendPasswordResetEmail(email: String){
        GlobalScope.launch(Dispatchers.Main) {
            when(val result = loginRepository.sendPasswordEmail(email)){
                is Result.Success -> resetPassword.value = R.string.send_reset_password_success
                is Result.Error ->  {
                    try {
                        val e = result.exception as FirebaseAuthInvalidUserException
                        when(e.errorCode){
                            "ERROR_USER_DISABLED" -> resetPassword.value = R.string.user_disable
                            else -> resetPassword.value = R.string.send_reset_password_email_error
                        }
                    }catch (e: Throwable) {
                        Log.e(TAG, "", result.exception)
                        resetPassword.value = R.string.send_reset_password_error
                    }
                }
                is Result.Canceled -> resetPassword.value = R.string.profile_creation_cancel
            }
        }
    }

    /**
     * *Coroutine*
     * Get User with uid from Firestore database
     * observe LoginViewModel.profileUserData to get result
     * @param uid String that corresponding to user uid
     */
    fun getFirestoreUser(uid: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = userRepository.getFirestoreUser(uid)
            userProfileDataChanged(result)
        }
    }

    /**
     * *Coroutine*
     * Create user profile to Firestore database
     * observe LoginViewModel.profileUserData to get result
     * @param user User object who contain user infos
     */
    fun createFirestoreUser(user: User){
        GlobalScope.launch(Dispatchers.Main) {
            when(userRepository.createFirestoreUser(user)){
                is Result.Success -> profileUser.value = InsertProfileState(isUserCreated = true)
                is Result.Error -> profileUser.value = InsertProfileState(error = R.string.profile_creation_fail)
                is Result.Canceled -> profileUser.value = InsertProfileState(error = R.string.profile_creation_cancel)
            }
        }
    }

    /**
     * Create private user info in user profile
     * set result to livaData
     * @param user User object who contain user infos
     */
    fun createPrivateUserInfo(userId: String, privateInfo: PrivateUserInfo){
        GlobalScope.launch(Dispatchers.Main) {
            when(userRepository.createPrivateUserInfo(userId, privateInfo)){
                is Result.Success -> profileUser.value = InsertProfileState(isUserCreated = true, isPrivateInfoCreated = true)
                is Result.Error -> profileUser.value = InsertProfileState(error = R.string.profile_creation_fail)
                is Result.Canceled -> profileUser.value = InsertProfileState(error = R.string.profile_creation_cancel)
            }
        }
    }

    /**
     * Manage result receive from Firebase auth coroutine
     * transform the result to liveDate<LoginUserState>
     * @param result Result<AuthResult> containing user auth information's
     */
    private fun loginUserStateChanged(result: Result<AuthResult>){
        when (result) {
            is Result.Success -> {
                val data = result.data
                val user = data.user
                loginUser.value = LoginUserState(user)
            }
            is Result.Error -> {
                Log.e(TAG, "Sign In error : ", result.exception)
                try {
                    val exp = result.exception as FirebaseAuthException
                    when (exp.errorCode) {
                        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                            loginUser.value =
                                LoginUserState(error = R.string.email_used_by_other_provider)
                        }
                        "ERROR_USER_DISABLED" -> {
                            loginUser.value =
                                LoginUserState(error = R.string.user_disable)
                        }
                        "ERROR_EMAIL_ALREADY_IN_USE" -> {
                            loginUser.value =
                                LoginUserState(error = R.string.email_already_used)
                        }
                        "ERROR_WRONG_PASSWORD", "ERROR_USER_NOT_FOUND" -> {
                            loginUser.value =
                                LoginUserState(error = R.string.invalid_user_password)
                        }
                        else -> {
                            loginUser.value =
                                LoginUserState(error = R.string.sign_in_fail)
                        }
                    }
                }
                catch (e:ClassCastException){
                    loginUser.value =
                        LoginUserState(error = R.string.sign_in_fail)
                }
            }
            is Result.Canceled -> loginUser.value =
                LoginUserState(error = R.string.sign_in_canceled)
        }

    }

    /**
     * From userProfileResult validation witch correct formatted email address and complex password
     * Change loginForm LiveData value
     * @param email email input text
     * @param password password input text
     */
    fun signUpFormDataChanged(email: String?, password: String?, confirmPassword: String? ){

        Log.d("Email =", email.toString())
        Log.d("password =", password.toString())
        Log.d("confirm =", confirmPassword.toString())
        when (true){
            !isValidEmail(email) ->
                loginForm.value = LoginFormState(emailError = R.string.invalid_email)
            !isPasswordValid(password) ->
                loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
            !password.equals(confirmPassword) ->
                loginForm.value = LoginFormState(confirmPassword = R.string.invalid_password_confirmation)
            else ->
                loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    /**
     * From userProfileResult validation witch correct formatted email address and not blank password
     * Change loginForm LiveData value
     * @param email email input text
     * @param password password input text
     */
    fun signInFormDataChanged(email: String, password: String){
        if (!isValidEmail(email)) {
            loginForm.value =
                LoginFormState(emailError = R.string.invalid_email)
        } else if (password.isBlank()) {
            loginForm.value =
                LoginFormState(passwordError = R.string.invalid_password)
        } else {
            loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    /**
     * Send result of LoginViewModel.getFirestoreUser to LoginViewModel.profileUserSate Live userProfileResult
     * Observe result with LoginViewModel.profileUserSate
     * @param result Result<DocumentSnapshot> value return by userRepository.getFirestoreUser(uid: String)
     */
    private fun userProfileDataChanged(result: Result<DocumentSnapshot>) = when(result) {
        is Result.Success -> {
            val user: User? = result.data.toObject(User::class.java)
            data.value = DataResult(data = user)
        }
        is Result.Error -> {
            Log.w(TAG,"Error when trying to get user from firestore", result.exception)
            data.value = DataResult(error = R.string.fetching_user_error)
        }
        is Result.Canceled ->  {
            Log.w(TAG,"Action canceled", result.exception)
            data.value = DataResult(error = R.string.fetching_user_canceled)
        }
    }

    /**
     * From userProfileResult validation witch correct formatted phone
     * Change profileForm LiveData value
     * @param phoneNumber phone input text
     */
    fun creationProfileDataChanged(gender: String?, username: String?, phoneNumber: String?) {

        if (!isGenderValid(gender))
            profileForm.value = ProfileFormState(genderError = R.string.invalid_gender)

        else if (!isUsernameValid(username))
            profileForm.value = ProfileFormState(usernameError = R.string.invalid_username_char)

        else if (!isPhoneNumberValid(phoneNumber))
            profileForm.value = ProfileFormState(phoneNumberError = R.string.invalid_phone_number)

        else
            profileForm.value = ProfileFormState(isDataValid = true)
    }

    /**
     * Inpput email userProfileResult validation witch correct formatted email string
     * @param email string address
     */
    fun addressEmailChanged(email: String) {
        if (!isValidEmail(email))
            emailValid.value = R.string.invalid_email
         else
            emailValid.value = null
    }

    /**
     * Valid the e-mail address passed in param
     * @param email input email text
     * @return Boolean that indicate if e-mail is valid or not
     */
    private fun isValidEmail(email: String?): Boolean {
        return if (email.isNullOrBlank()) false
        else email.matches(Regex("^([0-9a-zA-Z]([\\+\\-\\_\\.][0-9a-zA-Z]+)*)+@(([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]*\\.)+[a-zA-Z0-9]{2,17})\$"))
    }

    /**
     * Valid user first and last name
     * @param name input name text
     * @return Boolean that indicate if name is correctly formatted
     *
     */
    private fun isUsernameValid(name: String?): Boolean {
        return if (name.isNullOrBlank()) false
        else {
            if (name.length < 3 || name.length >100)
                return false
            else
                name.matches(Regex("^\\w+( \\w+)*\$")) // Only Alphanumeric
        }
    }

    /**
     * Valid the password passed in param with complex and strong validation
     * @param password input password text
     * @return Boolean that indicate if password is valid or not
     */
    private fun isPasswordValid(password: String?): Boolean{
        //TODO: Split regex to trigger specific error
        return if (password.isNullOrBlank()) false
        else return password.matches(Regex("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_]).*$"))
    }

    /**
     * Valid phone number format passed in param
     * @param phoneNumber input phone number text
     * @return Boolean that indicate if phone number is correctly formatted
     */
    private fun isPhoneNumberValid(phoneNumber: String?): Boolean{
        return if (phoneNumber.isNullOrBlank()) true
        else //French number format regex
            phoneNumber.matches(Regex("^(?:(?:\\+|00)33|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4}\$"))
        //TODO : add regex for other phone format
    }

    /**
     * Valid selected gender
     * @param gender is radio gender tag
     * @return Boolean that indicate if phone number is correctly formatted
     */
    private fun isGenderValid(gender: String?): Boolean {
        return !gender.isNullOrBlank()
        //TODO : Check gender string matches to gender values
    }

    /**
     * Verify User profile information and return user object
     * @param firebaseUser FirebaseUser?
     * @param displayName String? : displaying username
     * @param phoneNumber String? : user phone number
     * @param userCategory UserCategory? : category of user
     * @return User? object containing user infos or null if userProfileResult are invalid or missing
     */
    fun validUserInfo(firebaseUser: FirebaseUser?, username: String?): User? {

        if (firebaseUser != null){
            var user: User? = null

            try {
                user = User(
                    userId = firebaseUser.uid,
                    displayName = username!!,
                    description = null,
                    pseudo = generatePseudo(username)
                )
            } catch (e: NullPointerException){
                Log.e(TAG, "Cannot create User object : ", e)
            }
            return user
        }
        return null
    }

    /**
     * Verify private user information and return PrivateUserInfo object
     * @param firebaseUser is auth user information
     * @param gender is gender of user
     * @param phoneNumber is user phone number
     * @param birthDate is birthDate of user
     * @return PrivateUserInfo or null. PrivateUserInfo contain private user info like email, phone, etc
     */
    fun validPrivateUserInfo(firebaseUser: FirebaseUser?,gender: String?,
                             phoneNumber: String?, birthDate: Date?): PrivateUserInfo? {

        if (firebaseUser != null){
            var privateUserInfo: PrivateUserInfo? = null

            try {
                privateUserInfo = PrivateUserInfo(
                    gender = gender!!,
                    email = firebaseUser.email!!,
                    phoneNumber = phoneNumber,
                    birthDate = birthDate
                )
            } catch (e: NullPointerException){
                Log.e(TAG, "Cannot create PrivateUserInfo object : ", e)
            }
            return privateUserInfo
        }
        return null
    }

    /**
     * Generate pseudo basing on username values
     * Format pseudo for corresponding to standard pseudo format
     * example : #Random_Pseudo, #RanDomPseuDo3453 etc ...
     */
    private fun generatePseudo(username: String): String {
        var str = username.replace(Regex("[^a-zA-Z0-9_ ]"), "")
        str = str.replace(" ", "_")
        str = "#$str"
        return str
    }
}