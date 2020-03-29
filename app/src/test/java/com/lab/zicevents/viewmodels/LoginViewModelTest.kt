package com.lab.zicevents.viewmodels

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.lab.zicevents.data.database.user.UserDataSource
import com.lab.zicevents.data.database.user.UserRepository
import com.lab.zicevents.data.login.LoginDataSource
import com.lab.zicevents.data.login.LoginRepository
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.ui.login.LoginViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class LoginViewModelTest {
    private lateinit var loginViewModel: LoginViewModel
    private val context = mock(Context::class.java)
    private val mockedFirebaseOptions =
        FirebaseOptions.Builder().setApplicationId("zic-events").build()
    private val mockedFirestore = mock(FirebaseFirestore::class.java)
    private val mockedUserSource = UserDataSource(mockedFirestore)
    private val mockedFirebaseAuth = mock(FirebaseAuth::class.java)
    private val mockedLoginSource = LoginDataSource(mockedFirebaseAuth)



    @get:Rule // -> allows liveData to work on different thread
    val executorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {

        try {
            FirebaseApp.initializeApp(context, mockedFirebaseOptions)
        } catch (error: IllegalStateException) {
            Log.d(this::class.java.simpleName, "FirebaseApp already initialized")
        }

        loginViewModel = LoginViewModel(
            userRepository = UserRepository(
                userDataSource = mockedUserSource
            ),
            loginRepository = LoginRepository(
                loginDataSource =  mockedLoginSource
            )
        )
    }

    @Test
    fun checkIsEmailValidWithNullAddress(){
        assertFalse(loginViewModel.isValidEmail(null))
    }

    @Test
    fun checkIsEmailValid(){
        val validEmail = "email.test@domain.com"
        assertTrue(loginViewModel.isValidEmail(validEmail))
    }

    @Test
    fun checkIsEmailValidWithoutArobase(){
        val validEmail = "email.testdomain.com"
        assertFalse(loginViewModel.isValidEmail(validEmail))
    }

    @Test
    fun checkIsEmailValidWithoutDomainName(){
        val validEmail = "email.testdom@"
        assertFalse(loginViewModel.isValidEmail(validEmail))
    }

    @Test
    fun checkIsEmailValidWithInvalidDomainName(){
        var validEmail = "email.testdom@domain.g"
        assertFalse(loginViewModel.isValidEmail(validEmail))
        validEmail = "email.testdom@domain"
        assertFalse(loginViewModel.isValidEmail(validEmail))
    }

    @Test
    fun checkIsEmailValidWithSpecialInDomainName(){
        val validEmail = "email.testdom@-domain-.com"
        assertFalse(loginViewModel.isValidEmail(validEmail))
    }

    @Test
    fun checkIsEmailValidWithNumberInDomainName(){
        val validEmail = "email.testdom@89domain.com"
        assertTrue(loginViewModel.isValidEmail(validEmail))
    }

    @Test
    fun checkIsEmailValidWithInvalidSpecialChars() {
        val validEmail = "email.test/[dom@domain.com"
        assertFalse(loginViewModel.isValidEmail(validEmail))
    }

    @Test
    fun checkIsEmailValidWithValidSpecialChars() {
        val validEmail = "email-test@domain.com"
        assertTrue(loginViewModel.isValidEmail(validEmail))
    }

    @Test
    fun checkIsEmailValidWithCaps() {
        val validEmail = "Email.Test@doMain.com"
        assertTrue(loginViewModel.isValidEmail(validEmail))
    }

    /*********************************/

    @Test
    fun checkIsPasswordValidWithNullOrBlankValue(){
        assertFalse(loginViewModel.isPasswordValid(null))
        assertFalse(loginViewModel.isPasswordValid(""))
    }

    @Test
    fun checkIsPasswordValid(){
        val password = "Ef85/£_zer5Ft_8"
        assertTrue(loginViewModel.isPasswordValid(password))
    }

    @Test
    // Min 8 chars
    fun checkIsPasswordValidWithTooShortPassword(){
        val password = "Ef85"
        assertFalse(loginViewModel.isPasswordValid(password))
    }

    @Test
    // Min 1 special char [@#$%^&+=_}
    fun checkIsPasswordValidWithoutSpecialChars(){
        val password = "Ef8fjjTb7djY54sd"
        assertFalse(loginViewModel.isPasswordValid(password))
    }

    @Test
    // Min 1 caps char
    fun checkIsPasswordValidWithoutCapsChars(){
        val password = "ffg84/f_y54-91$"
        assertFalse(loginViewModel.isPasswordValid(password))
    }

    @Test
    // Min 1 numeric char
    fun checkIsPasswordValidWithoutNumber(){
        val password = "ffgPRJ/f_yAc-Pz$"
        assertFalse(loginViewModel.isPasswordValid(password))
    }

    /*********************************/

    @Test
    fun checkIsUsernameValidWithNullOrBlankValue(){
        assertFalse(loginViewModel.isUsernameValid(null))
        assertFalse(loginViewModel.isUsernameValid(""))
    }

    @Test
    fun checkIsUsernameValid(){
        val username = "Guillaume BAGUE"
        assertTrue(loginViewModel.isUsernameValid(username))
    }

    @Test
    // Max 100 chars
    fun checkIsUsernameValidWithTooLongString(){
        val username = "Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum " +
                "Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum"
        assertFalse(loginViewModel.isUsernameValid(username))
    }

    @Test
    // Min 3 chars
    fun checkIsUsernameValidWithTooShortString(){
        val username = "Gu"
        assertFalse(loginViewModel.isUsernameValid(username))
    }

    @Test
    fun checkIsUsernameValidWithSpecialChars(){
        val username = "Guillaum$=BAGUE"
        assertFalse(loginViewModel.isUsernameValid(username))
    }

    @Test
    fun checkIsUsernameValidWithNumber(){
        val username = "GuillaumeBAGUE7845"
        assertTrue(loginViewModel.isUsernameValid(username))
    }

    /*********************************/

    @Test
    fun checkIsPhoneNumberValidWithNullOrBlankValue(){
        assertTrue(loginViewModel.isPhoneNumberValid(null))
        assertTrue(loginViewModel.isPhoneNumberValid(""))
    }

    @Test
    fun checkIsPhoneNumberValid(){
        var phoneNumber = "0386459635"
        assertTrue(loginViewModel.isPhoneNumberValid(phoneNumber))
        phoneNumber = "0786459635"
        assertTrue(loginViewModel.isPhoneNumberValid(phoneNumber))
        phoneNumber = "0886459635"
        assertTrue(loginViewModel.isPhoneNumberValid(phoneNumber))
        phoneNumber = "0986459635"
        assertTrue(loginViewModel.isPhoneNumberValid(phoneNumber))
        phoneNumber = "0186459635"
        assertTrue(loginViewModel.isPhoneNumberValid(phoneNumber))
    }

    @Test
    fun checkIsPhoneNumberValidWithTooShortLength(){
        val phoneNumber = "06"
        assertFalse(loginViewModel.isPhoneNumberValid(phoneNumber))
    }

    @Test
    fun checkIsPhoneNumberValidWithTooLongLength(){
        val phoneNumber = "0654654651654665613515316351351"
        assertFalse(loginViewModel.isPhoneNumberValid(phoneNumber))
    }

    @Test
    fun checkIsPhoneNumberValidWithSpecialChars(){
        val phoneNumber = "03864*5963*5"
        assertFalse(loginViewModel.isPhoneNumberValid(phoneNumber))
    }

    @Test
    fun checkIsPhoneNumberValidTelephoneCode(){
        // +33
        var phoneNumber = "+33601430299"
        assertTrue(loginViewModel.isPhoneNumberValid(phoneNumber))
        // 0033
        phoneNumber = "0033601430299"
        assertTrue(loginViewModel.isPhoneNumberValid(phoneNumber))
    }

    @Test
    // Phone number should begin with 0[1-9]
    fun checkIsPhoneNumberValidWithNonZeroFirstNumber(){
        val phoneNumber = "7845451235"
        assertFalse(loginViewModel.isPhoneNumberValid(phoneNumber))
    }

    /*********************************/

    @Test
    fun checkGeneratePseudo(){
        var username = "Guillaume BAGUE"
        var expectedPseudo = "#Guillaume_BAGUE"
        assertEquals(expectedPseudo, loginViewModel.generatePseudo(username))
        // with number
        username = "Guillaume BAGUE7892"
        expectedPseudo = "#Guillaume_BAGUE7892"
        assertEquals(expectedPseudo, loginViewModel.generatePseudo(username))
    }

    @Test
    // Remove all special chars expect _
    fun checkGeneratePseudoWithSpecialChars(){
        val username = "_Guillaume£%/*-#"
        val expectedPseudo = "#_Guillaume"
        assertEquals(expectedPseudo, loginViewModel.generatePseudo(username))
    }
/*
    @Test
    fun checkGeneratePseudoWithConsecutiveWhiteSpace(){
        val username = "guillaume   bague"
        val expectedPseudo = "#guillaume_bague"
        assertEquals(expectedPseudo, loginViewModel.generatePseudo(username))
    }
*/

    /*********************************/

    @Test
    fun checkValidUserInfoWithNullFirebaseUser() {
        val username = "Guillaume BAGUE"
        assertNull(loginViewModel.validUserInfo(null, username))
    }

    @Test
    fun checkValidUserInfoWithNullUsername() {
        val authUser = mockedFirebaseAuth.currentUser
        assertNull(loginViewModel.validUserInfo(authUser, null))
    }
}