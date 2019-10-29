package com.lab.zicevents.ui.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.lab.zicevents.R
import kotlinx.android.synthetic.main.fragment_login_main.*

/**
 * Main Screen [Fragment] class.
 */
class LoginMainFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login_main_sign_in.setOnClickListener(this)
        login_main_sign_up.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            login_main_sign_in.id -> findNavController().navigate(R.id.action_navigation_login_main_to_navigation_login_sign_in)
            login_main_sign_up.id -> findNavController().navigate(R.id.action_navigation_login_main_to_navigation_login_sign_up)
        }
    }
}
