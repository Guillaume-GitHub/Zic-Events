package com.lab.zicevents.ui.login

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lab.zicevents.R
import com.lab.zicevents.data.login.LoginRepository
import com.lab.zicevents.data.Result
import com.lab.zicevents.data.database.UserRepository
import com.lab.zicevents.data.model.database.User
import com.lab.zicevents.data.model.local.*
import kotlinx.coroutines.*
import java.lang.ClassCastException
import kotlin.contracts.Returns

class LoginViewModel(private val loginRepository: LoginRepository, private val userRepository: UserRepository): ViewModel() {
    
    private val TAG = this::class.java.simpleName

    private val loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = loginForm

    private val loginUser = MutableLiveData<LoginUserState>()
    val loginUserState: LiveData<LoginUserState> = loginUser

    private val profileUser = MutableLiveData<ProfileUserState>()
    val profileUserState: LiveData<ProfileUserState> = profileUser

    private var userTypeList: ArrayList<UserCategory>? = null

    private val profileForm = MutableLiveData<ProfileCreationFormState>()
    val profileFormState: LiveData<ProfileCreationFormState> = profileForm

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
     * *Coroutine*
     * Get User with uid from Firestore database
     * observe LoginViewModel.profileUserState to get result
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
     * observe LoginViewModel.profileUserState to get result
     * @param user User object who contain user infos
     */
    fun createFirestoreUser(user: User){
        GlobalScope.launch(Dispatchers.Main) {
            when(userRepository.createFirestoreUser(user)){
                is Result.Success -> profileUser.value = ProfileUserState(firestoreUser = user)
                is Result.Error -> profileUser.value = ProfileUserState(error = R.string.profile_creation_fail)
                is Result.Canceled -> profileUser.value = ProfileUserState(error = R.string.profile_creation_cancel)
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
     * From data validation witch correct formatted email address and complex password
     * Change loginForm LiveData value
     * @param email email input text
     * @param password password input text
     */
    fun signUpFormDataChanged(email: String, password: String){
        if (!isValidEmail(email)) {
            loginForm.value =
                LoginFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            loginForm.value =
                LoginFormState(passwordError = R.string.invalid_password)
        } else {
            loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    /**
     * From data validation witch correct formatted email address and not blank password
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
     * Send result of LoginViewModel.getFirestoreUser to LoginViewModel.profileUserSate Live data
     * Observe result with LoginViewModel.profileUserSate
     * @param result Result<DocumentSnapshot> value return by userRepository.getFirestoreUser(uid: String)
     */
    private fun userProfileDataChanged(result: Result<DocumentSnapshot>) = when(result) {
        is Result.Success -> {
            val user: User? = result.data.toObject(User::class.java)
            profileUser.value = ProfileUserState(firestoreUser = user)
        }
        is Result.Error -> {
            Log.w(TAG,"Error when trying to get user from firestore", result.exception)
            profileUser.value = ProfileUserState(error = R.string.fetching_user_error)
        }
        is Result.Canceled ->  {
            Log.w(TAG,"Action canceled", result.exception)
            profileUser.value = ProfileUserState(error = R.string.fetching_user_canceled)
        }
    }

    /**
     * From data validation witch correct formatted phone and user category
     * Change profileForm LiveData value
     * @param phoneNumber phone input text
     * @param category userCategory id
     */
    fun creationProfileDataChanged(phoneNumber: String? = null, category: UserCategory?){
        if (!isPhoneNumberValid(phoneNumber)) {
            profileForm.value = ProfileCreationFormState(phoneNumberError = R.string.invalid_phone_number)
        } else if (category == null) {
            profileForm.value = ProfileCreationFormState(userTypeError = R.string.invalid_user_category)
        } else {
            profileForm.value = ProfileCreationFormState(isDataValid = true)
        }
    }

    /**
     * Valid the e-mail address passed in param
     * @param email input email text
     * @return Boolean that indicate if e-mail is valid or not
     */
    private fun isValidEmail(email: String): Boolean{
        return when (true){
            email.contains("@") -> Patterns.EMAIL_ADDRESS.matcher(email).matches()
            else -> email.isBlank()
        }
    }

    /**
     * Valid the password passed in param with complex and strong validation
     * @param password input password text
     * @return Boolean that indicate if password is valid or not
     */
    private fun isPasswordValid(password: String): Boolean{
        //TODO: Split regex to trigger specific error
        return password.matches(Regex("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_]).*$"))
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
     * Verify User profile information and return user object
     * @param firebaseUser FirebaseUser?
     * @param displayName String? : displaying username
     * @param phoneNumber String? : user phone number
     * @param userCategory UserCategory? : category of user
     * @return User? object containing user infos or null if data are invalid or missing
     */
    fun validUserInfo(firebaseUser: FirebaseUser?,
                      displayName: String?, phoneNumber: String?, userCategory: UserCategory?): User? {
        var user: User? = null
        val username = displayName ?: firebaseUser?.displayName

        try {
            user = User(firebaseUser!!.uid, username!! ,firebaseUser.photoUrl?.path, phoneNumber, userCategory?.id!!)

        } catch (e: NullPointerException){
            Log.e(TAG, "Error on valid user infos", e)
        }
        return user
    }

    /**
     * Return all user category
     * @param context
     * @return ArrayList<UserCategory>
     */
    fun getUserType(context: Context): ArrayList<UserCategory>{
        var arrayList = userTypeList

        if (arrayList == null) {
            val array = context.resources.getTextArray(R.array.user_category)
            val type = object : TypeToken<UserCategory>(){}.type
            arrayList = ArrayList()

            for (json in array){
                val map: UserCategory = Gson().fromJson(json.toString(), type)
                arrayList.add(map)
            }
            userTypeList = arrayList
        }
        return arrayList
    }
}



