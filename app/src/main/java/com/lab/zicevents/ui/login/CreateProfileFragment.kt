package com.lab.zicevents.ui.login

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.lab.zicevents.MainActivity

import com.lab.zicevents.R
import com.lab.zicevents.data.model.local.UserCategory
import com.lab.zicevents.data.service.AddressResultReceiver
import com.lab.zicevents.data.service.FetchAddressIntentService
import kotlinx.android.synthetic.main.fragment_create_profile.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Creation profile [Fragment]
 */
class CreateProfileFragment : Fragment(), View.OnClickListener, View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener {

    private val args: CreateProfileFragmentArgs by navArgs()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var pseudo: String
    private lateinit var email: String
    private var phone: String? = null
    private val calendar = Calendar.getInstance()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private var resultReceiver: AddressResultReceiver = AddressResultReceiver(Handler())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getArgs()

        configureViewModel()
        configClickableViews()
        configFocusableViews()

        bindViews()

        observeDataFormState()
        observeProfileCreationSate()

        // Set TextWatcher to input phone Number
        create_profile_phone.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(text: Editable?) {
                phone = text.toString()
                loginViewModel.creationProfileDataChanged(phoneNumber = phone, category = UserCategory(null,null))
            }
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        // Get google Location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
    }

    /**
     * Trigger Click on views
     */
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.create_profile_next -> createUserProfile()
            R.id.create_profile_location_btn -> fetchAddress()
            else -> {}
        }
    }

    /**
     * Trigger Focus on views
     */
    override fun onFocusChange(view: View?, isFocused: Boolean) {
        when(view?.id){
            R.id.create_profile_date -> {
                if(isFocused) {
                    hideSoftKeyboard(view)
                    showDatePicker()
                }
            }
        }
    }

    /**
     * Get Date selected from DatePicker  and display it into date input
     * @param view correspond to DatePicker
     * @param year is int corresponding to year of date picked
     * @param month is int corresponding to month of date picked
     * @param day is int corresponding to day of date picked
     */
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)

        val formatter = SimpleDateFormat("d MMMM yyyy", Locale.FRENCH)
        create_profile_date.setText(formatter.format(calendar.time))
    }

    /**
     * Get args passed to bundle
     */
    private fun getArgs(){
        pseudo = args.pseudo
        email = args.email
        phone = args.phoneNumber
    }

    /**
     * Bind views
     */
    private fun bindViews(){
        displayTitle(pseudo)
        displayPhoneNumber(phone)
    }

    /**
     *  Instantiate a LoginViewModel class from LoginViewModelFactory
     */
    private fun configureViewModel(){
        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory()).get(LoginViewModel::class.java)
    }

    /**
     *  Set OnClickListener on clickable Views
     */
    private fun configClickableViews(){
        create_profile_next.setOnClickListener(this)
        create_profile_location_btn.setOnClickListener(this)
    }

    /**
     *  Set OnFocusChangeListener on focusable Views
     */
    private fun configFocusableViews(){
        create_profile_date.onFocusChangeListener = this
    }

    /**
     *  Hide soft Keyboard and clean focus on view
     *  @param view is view currently focus
     */
    private fun hideSoftKeyboard(view: View?){
        view?.clearFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    /**
     * Display pseudo or username passed to bundle in title
     * @param pseudo is username or pseudo of user
     */
    private fun displayTitle(pseudo: String) {
        create_profile_title.apply {
            val text =  getString(R.string.welcome) + " $pseudo"
            this.text = text
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
     * Show a DatePickerDialog
     */
    private fun showDatePicker(){
       DatePickerDialog(context!!, R.style.AppTheme_DatePickerDialog ,this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))
           .show()
    }

    /**
     * Display replace location drawable by progress bar in Location input text and vice versa
     */
    private fun showProgressGeolocation(isVisible: Boolean){
        if (isVisible){
            create_profile_location_btn.visibility = View.GONE
            create_profile_location_progress.visibility = View.VISIBLE

        } else {
            create_profile_location_btn.visibility = View.VISIBLE
            create_profile_location_progress.visibility = View.GONE
        }
    }


    private fun createUserProfile(){
        val user = loginViewModel.validUserInfo(auth.currentUser, pseudo,phone,
            UserCategory(0,"default")
        )
        if (user != null)
            loginViewModel.createFirestoreUser(user)
        else
            Toast.makeText(context,getText(R.string.incomplete_profile_info),Toast.LENGTH_LONG).show()
    }

    /**
     * Observe form state and display errors messages if data are not valid
     * Set submit button Enable / Disable
     */
    private fun observeDataFormState(){
        loginViewModel.profileFormState.observe(this, Observer {
            val dataState = it

            create_profile_next.isEnabled = dataState.isDataValid

            if (dataState.phoneNumberError != null) create_profile_input_layout_phone.error = getString(dataState.phoneNumberError)
            else create_profile_input_layout_phone.error = null

           // if (dataState.userTypeError != null) create_profile_input_layout_type.error = getString(dataState.userTypeError)
           // else create_profile_input_layout_type.error = null
        })
    }

    /**
     * Observe profile creation to firestore
     * Start MainActivity if user is not null else display errors
     */
    private fun observeProfileCreationSate(){
        loginViewModel.profileUserData.observe(this, Observer {
            val profileState = it

            if (profileState.firestoreUser != null){
                startActivity(Intent(context,MainActivity::class.java))
                activity?.finish()

            } else if (profileState.error != null) {
                Toast.makeText(context, getString(profileState.error),Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Start FetchAddressService to get address corresponding to device position
     */
    private fun startIntentService() {
        val intent = Intent(context, FetchAddressIntentService::class.java).apply {
            putExtra(FetchAddressIntentService.Constants.RECEIVER, resultReceiver)
            putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, lastKnownLocation)
        }
        context!!.startService(intent)
    }

    /**
     * Get Device location
     */
    private fun fetchAddress() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            lastKnownLocation = location

            if (lastKnownLocation == null) return@addOnSuccessListener

            if (!Geocoder.isPresent()) {
                Toast.makeText(context, "Geocoder not available", Toast.LENGTH_LONG).show()
                return@addOnSuccessListener
            }

            startIntentService()
        }
    }


}

