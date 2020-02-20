package com.lab.zicevents.ui.login

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.lab.zicevents.activity.MainActivity

import com.lab.zicevents.R
import kotlinx.android.synthetic.main.fragment_create_profile.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Creation profile [Fragment]
 */
class CreateProfileFragment : Fragment(), View.OnClickListener, View.OnFocusChangeListener,
    DatePickerDialog.OnDateSetListener,RadioGroup.OnCheckedChangeListener{

    private val args: CreateProfileFragmentArgs by navArgs()
    private val auth = FirebaseAuth.getInstance()
    private val calendar = Calendar.getInstance()
    private var progressDialog: Dialog? = null
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var email: String
    private var username: String? = null
    private var gender: String? = null
    private var phone: String? = null
    private var birthDate: Date? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgs()
        configureViewModel()
        setRadioButtonTags()
        create_profile_next.setOnClickListener(this)
        create_profile_date.onFocusChangeListener = this
        create_profile_radioGroup.setOnCheckedChangeListener(this)
        observeDataFormState()
        observeProfileCreationSate()
        bindViews()

        // Set TextWatcher to input phone Number
        create_profile_phone.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(text: Editable?) {
                if (text != null) {
                    phone = text.toString()
                    loginViewModel.creationProfileDataChanged(phoneNumber = phone, gender = gender, username = username)
                }
            }
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        // Set TextWatcher to input username
        create_profile_username.addTextChangedListener(object :TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                if (text != null) {
                    username = text.toString()
                    loginViewModel.creationProfileDataChanged(username = username, gender = gender, phoneNumber = phone)
                }
            }
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

    }

    /**
     * Get args passed to bundle
     */
    private fun getArgs(){
        username = args.username
        email = args.email
        phone = args.phoneNumber
    }

    /**
     * Trigger Click on views
     */
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.create_profile_next -> createUserProfile()
            else -> {}
        }
    }

    /**
     * Trigger Focus change on views
     */
    override fun onFocusChange(view: View?, isFocused: Boolean) {
        when(view?.id){
            R.id.create_profile_date ->
                if (isFocused) {
                    hideSoftKeyboard(view)
                    showDatePicker()
                }
            else -> {}
        }
    }

    /**
     * Set tags to Radio Buttons
     */
    private fun setRadioButtonTags(){
        create_profile_gender_male_radio.tag = getString(R.string.gender_male_value)
        create_profile_gender_female_radio.tag = getString(R.string.gender_female_value)
        create_profile_gender_other_radio.tag = getString(R.string.gender_other_value)
    }

    /**
     * Get Checked radio in radio group
     */
    override fun onCheckedChanged(radioGroup: RadioGroup?, radioId: Int) {
        if (radioGroup != null && radioGroup.id == create_profile_radioGroup.id) {
           when(radioId){
               create_profile_gender_female_radio.id -> gender = create_profile_gender_female_radio.tag.toString()
               create_profile_gender_male_radio.id -> gender = create_profile_gender_male_radio.tag.toString()
               create_profile_gender_other_radio.id -> gender = create_profile_gender_other_radio.tag.toString()
           }
            loginViewModel.creationProfileDataChanged(gender = gender, username = username, phoneNumber = phone)
        }
    }

    /**
     * Bind views
     */
    private fun bindViews(){
        displayUsername()
        displayPhoneNumber(phone)
    }

    /**
     *  Instantiate a LoginViewModel class from LoginViewModelFactory
     */
    private fun configureViewModel(){
        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory()).get(LoginViewModel::class.java)
    }


    /**
     * Try to get pseudo from auth user
     * display it to input username
     * set value to username var
     */
    private fun displayUsername() {
        create_profile_username.apply {
            username = FirebaseAuth.getInstance().currentUser?.displayName
            if (username != null) setText(username.toString())
        }
    }

    /**
     * Display not null phone number passed to bundle in input phone
     * @param phone is phone number as string. May be null
     */
    private fun displayPhoneNumber(phone: String?){
        create_profile_phone.apply {
            if (phone != null) this.setText(phone)
        }
    }

    /**
     * Create user object according to form values
     */
    private fun createUserProfile(){
        val user = loginViewModel.validUserInfo(auth.currentUser, username)
        val privateUserInfo = loginViewModel.validPrivateUserInfo(auth.currentUser,gender ,phone, birthDate)

        if (user != null && privateUserInfo != null)  {
            loginViewModel.createFirestoreUser(user)
            showDialog(true)
        }
        else
            Toast.makeText(context,getText(R.string.incomplete_profile_info),Toast.LENGTH_LONG).show()
    }

    /**
     * Observe profile creation to firestore
     * Start MainActivity if user is not null else display errors
     */
    private fun observeProfileCreationSate() {
        loginViewModel.insertProfileState.observe(this, Observer {
            val profileState = it

            when {
                // Case User profile are created but not private info
                profileState.isUserCreated && !profileState.isPrivateInfoCreated -> {

                    val privateUserInfo = loginViewModel.validPrivateUserInfo(auth.currentUser,gender ,phone, birthDate)
                    val userId = auth.uid

                    if (privateUserInfo != null && userId != null)
                        loginViewModel.createPrivateUserInfo(userId, privateUserInfo)
                    else  {
                        Log.d(this.javaClass.name,"Error Before send private user info")
                        showDialog(false)
                    }
                }
                // Case user profile + private info created
                profileState.isUserCreated && profileState.isPrivateInfoCreated -> {
                    showDialog(false)
                    startActivity(Intent(context, MainActivity::class.java))
                    activity?.finish()
                }
                // Case error
                profileState.error != null -> {
                    showDialog(false)
                    Toast.makeText(context, getString(profileState.error),Toast.LENGTH_SHORT).show()
                }
                else ->{ }
            }
        })
    }

    /**
     * Observe form state and display errors messages if userProfileResult are not valid
     * Set submit button Enable / Disable
     */
    private fun observeDataFormState(){
        loginViewModel.profileFormState.observe(this, Observer {
            val dataState = it

            create_profile_next.isEnabled = dataState.isDataValid

            if (dataState.genderError != null) create_profile_gender_error.text = getString(dataState.genderError)
            else create_profile_gender_error.text = null

            if (dataState.usernameError != null) create_profile_input_layout_username.error = getString(dataState.usernameError)
            else create_profile_input_layout_username.error = null

            if (dataState.phoneNumberError != null) create_profile_input_layout_phone.error = getString(dataState.phoneNumberError)
            else create_profile_input_layout_phone.error = null

        })
    }

    /**
     * Hide soft keyboard
     * @param view is view focused
     */
    private fun hideSoftKeyboard(view: View?){
        view?.clearFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken,0)
    }

    /**
     * Configure and Show Date Picker
     */
    private fun showDatePicker(){
       val dialog = DatePickerDialog(context!!,R.style.AppTheme_DatePickerDialog,this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dialog.show()
    }

    /**
     * Get selected date from DatePickerDialog and display value in input date
     */
    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)

        birthDate = calendar.time

        // Format date String before display
        val dateFormat = SimpleDateFormat.getDateInstance()
        create_profile_date.setText(dateFormat.format(calendar.time))
    }

    /**
     * Display custom dialog with progress bar
     * @param display
     */
    private fun showDialog(display: Boolean){
        if (display){
            val dialog = Dialog(context!!)
            progressDialog = dialog
            val rootView = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog, null)
            val messageTextView: TextView = rootView.findViewById(R.id.custom_progress_dialog_message)

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // hide window
            messageTextView.text = getString(R.string.update_profile_dialog_message) // set message
            dialog.setContentView(rootView) // add custom view
            dialog.setCancelable(false)
            dialog.show()

        } else {
            this.progressDialog?.dismiss()
        }
    }

}

