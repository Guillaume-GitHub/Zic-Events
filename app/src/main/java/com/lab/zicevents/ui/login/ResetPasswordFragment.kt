package com.lab.zicevents.ui.login

import android.app.Dialog
import android.os.Bundle
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

        // Set click listener
        reset_password_fragment_send_btn.setOnClickListener {
            sendResetEmailLink()
        }
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
     * Send email with link to reset password
     * Display progress dialog
     */
    private fun sendResetEmailLink(){
        showDialog(true)
        //TODO : Check and valid input email text before send to loginViewModel.sendPasswordResetEmail() method
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
