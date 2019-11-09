package com.lab.zicevents.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs

import com.lab.zicevents.R
import kotlinx.android.synthetic.main.fragment_create_profile.*


/**
 * A simple [Fragment] subclass.
 */
class CreateProfileFragment : Fragment() {

   private val args: CreateProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        create_profile_title.text = "Bonjour, ${args.pseudo}"
        create_profile_email.setText(args.email)
    }
}
