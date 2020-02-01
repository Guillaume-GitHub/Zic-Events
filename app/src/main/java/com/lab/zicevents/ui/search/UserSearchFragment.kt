package com.lab.zicevents.ui.search

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.lab.zicevents.R
import com.lab.zicevents.data.model.database.user.User
import com.lab.zicevents.utils.MarginItemDecoration
import com.lab.zicevents.utils.adapter.SearchUserRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_user_search.*

/**
 * A simple [Fragment] subclass.
 */
class UserSearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchUserRecycler: RecyclerView
    private lateinit var searchUserAdapter: SearchUserRecyclerAdapter
    private var userList = ArrayList<User>()

    private var handler: Handler = Handler()
    private var runnable: Runnable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        configureRecyclerView()
        observeUsersSearchResult()

        user_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                if (!text.isNullOrBlank()){
                    val r = Runnable { performSearch(text.toString()) }
                    runnable = r
                    handler.postDelayed(r, 500)
                } else
                    updateUI(null)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                runnable?.let {
                    handler.removeCallbacks(it)
                }
            }
        })
    }

    private fun initViewModel(){
        this.searchViewModel = ViewModelProviders
            .of(this, SearchViewModelFactory())
            .get(SearchViewModel::class.java)
    }

    private fun updateUI(list: List<User>?){
        // Clean list
        userList.clear()
        // Add user in list if not null
        list?.let {
            userList.addAll(it)
        }
        // Notify adapter
        searchUserAdapter.notifyDataSetChanged()
    }

    private fun configureRecyclerView(){
        searchUserRecycler = search_recyclerView
        searchUserRecycler.layoutManager = LinearLayoutManager(context)
        searchUserAdapter = SearchUserRecyclerAdapter(context!!, userList)
        searchUserRecycler.adapter = searchUserAdapter
        searchUserRecycler.addItemDecoration(MarginItemDecoration(20))
    }

    private fun performSearch(query: String){
        Log.d(this::class.java.simpleName, "Search user containing $query")
        searchViewModel.searchUsers(query)
    }

    private fun observeUsersSearchResult(){
        searchViewModel.searchUsersResult.observe(this, Observer {
            when {
                it.data is List<*> -> {
                    val list: List<User> = it.data.filterIsInstance(User::class.java)
                    updateUI(list)
                }
            }
        })
    }

}
