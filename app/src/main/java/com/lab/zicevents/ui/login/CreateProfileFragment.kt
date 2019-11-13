package com.lab.zicevents.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs

import com.lab.zicevents.R
import com.lab.zicevents.data.model.local.UserCategory
import com.lab.zicevents.utils.adapter.UserCategoryAdapter
import kotlinx.android.synthetic.main.fragment_create_profile.*


/**
 * Creation profile [Fragment]
 */
class CreateProfileFragment : Fragment(), View.OnClickListener, AdapterView.OnItemClickListener {

    private val args: CreateProfileFragmentArgs by navArgs()
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var pseudo: String
    private lateinit var email: String
    private var phone: String? = null
    private var userType: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pseudo = args.pseudo
        email = args.email
        phone = args.phoneNumber

        configureViewModel()
        displayTitle(pseudo)
        displayPhoneNumber(phone)
        fillCategoryDropDown(loginViewModel.getUserType(context!!))
        observeDataFormState()

        // Set input button click listener
        create_profile_next.setOnClickListener(this)
        // Set TextWatcher to input phone Number
        create_profile_phone.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(text: Editable?) {
                loginViewModel.creationProfileDataChanged(phoneNumber = text.toString(), category = userType)
            }
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

    }

    /**
     * Trigger Click on views
     */
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.create_profile_next -> Log.d("Tag", "isClicked") //TODO : Firebase Profile Creation
            else -> {}
        }
    }

    /**
     * Get the selected item in dropdown list, display category name in category autocompleteTextView
     * and notify viewModel that data form changed
     */
    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, position: Int, p3: Long) {
        val category = adapterView?.getItemAtPosition(position) as UserCategory
        // Display category name of selected item in autocompleteTextView
        create_profile_type.setText(category.category)

        // Pass id to userType var
        if (category.id != null){
            userType = category.id
            loginViewModel.creationProfileDataChanged(phoneNumber = null, category = userType)
        }
    }

    /**
     *  Instantiate a LoginViewModel class from LoginViewModelFactory
     */
    private fun configureViewModel(){
        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory()).get(LoginViewModel::class.java)
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
     * Create Custom ArrayAdapter with data passed in params and show dropDown list with items
     * when autocompleteTextView are clicked or focused
     * Get the id of selected item and pass it to userType var
     * @param list = ArrayList<UserCategory>
     */
    private fun fillCategoryDropDown(list: ArrayList<UserCategory>){
        val adapter = UserCategoryAdapter(context!!, list) // Create adapter
        create_profile_type.setAdapter(adapter)// set adapter to autocompleteTextView
        create_profile_type.threshold = 50 // set number of letter before autocomplete -> always show all item in list
        create_profile_type.dropDownAnchor = R.id.create_profile_dropdown_anchor

        create_profile_type.setOnClickListener {
            create_profile_type.showDropDown() //Show drop down List
        }

        create_profile_type.setOnFocusChangeListener { _, isFocus ->
            if (isFocus) create_profile_type.showDropDown() //Show drop down List
            //TODO : Hide keyboard
        }
        create_profile_type.onItemClickListener = this
    }

    /**
     * Observe form state and display errors messages if data are not valid
     * Set submit button Enable / Disable
     */
    private fun observeDataFormState(){
        loginViewModel.profileformState.observe(this, Observer {
            val dataState = it

            create_profile_next.isEnabled = dataState.isDataValid

            if (dataState.phoneNumberError != null) create_profile_input_layout_phone.error = getString(dataState.phoneNumberError)
            else create_profile_input_layout_phone.error = null

            if (dataState.userTypeError != null) create_profile_input_layout_type.error = getString(dataState.userTypeError)
            else create_profile_input_layout_type.error = null
        })
    }
}

