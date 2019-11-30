package com.lab.zicevents.ui.login

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.lab.zicevents.R
import kotlinx.android.synthetic.main.fragment_reset_password.*

/**
 * Simple Reset Password [Fragment] subclass.
 */
class ResetPasswordFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private var progressDialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureViewModel()
        observeResetPasswordMail()
        observeEmailValidation()

        // Set click listener
        reset_password_fragment_send_btn.setOnClickListener {
            sendResetEmailLink()
        }

        // Set input email text watcher
        reset_password_fragment_email.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(text: Editable?) {
                if (!text.isNullOrBlank())
                    loginViewModel.addressEmailChanged(text.toString())
            }
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    /**
     * Instantiate a LoginViewModel class from LoginViewModelFactory
     */
    private fun configureViewModel(){
        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory()).get(LoginViewModel::class.java)
    }

    /**
     * Observe sending reset password email state
     * Get int to display state message
     */
    private fun observeResetPasswordMail(){
        loginViewModel.resetPasswordStatus.observe(this, Observer {
            showDialog(false)
            Toast.makeText(context, getText(it), Toast.LENGTH_LONG).show()
        })
    }

    /**
     * Observe email address validation
     * Get int to display error message
     */
    private fun observeEmailValidation(){
        loginViewModel.emailValidState.observe(this, Observer {
            if (it != null) {
                reset_password_fragment_inputLayout_email.error = getString(it) // set error message
                reset_password_fragment_send_btn.isEnabled = false // disable btn
            } else {
                reset_password_fragment_inputLayout_email.error = null // clean error message
                reset_password_fragment_send_btn.isEnabled = true // enable btn
            }
        })
    }

    /**
     * Send email with link to reset password
     * Display progress dialog
     */
    private fun sendResetEmailLink(){
        showDialog(true)
        loginViewModel.sendPasswordResetEmail(reset_password_fragment_email.text.toString())
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
            messageTextView.text = getString(R.string.send_reset_password_link) // set message
            dialog.setContentView(rootView) // add custom view
            dialog.setCancelable(false)
            dialog.show()

        } else{
            this.progressDialog?.dismiss()
        }
    }
}
