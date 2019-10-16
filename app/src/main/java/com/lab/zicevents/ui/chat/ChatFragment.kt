package com.lab.zicevents.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lab.zicevents.R
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment() {

    private lateinit var chatViewModel: ChatViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initViewModel()
        this.bindView()
    }

    private fun initViewModel(){
        this.chatViewModel = ViewModelProviders.of(this).get(ChatViewModel::class.java)
    }

    private fun bindView(){
        this.chatViewModel.fragmentName.observe(this, Observer { text ->
            text_chat.text = text
        })
    }
}