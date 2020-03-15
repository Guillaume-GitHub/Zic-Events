package com.lab.zicevents.ui.event

import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lab.zicevents.R
import com.lab.zicevents.data.model.api.songkick.Artist
import com.lab.zicevents.utils.OnRecyclerItemClickListener
import com.lab.zicevents.utils.adapter.ArtistRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_artist_search.*

/**
 * A simple [Fragment] subclass.
 */
class ArtistSearchFragment : DialogFragment(), OnRecyclerItemClickListener {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var adapter: ArtistRecyclerAdapter
    private var artistList = ArrayList<Artist>()
    private var handler: Handler = Handler()
    private var runnable: Runnable? = null
    private val NB_RESULT = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.AppTheme_FullScreenDialog)
        retainInstance = true
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureRecyclerView()
        observeArtistResults()

        fragment_artist_search_searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank() && query.length >= 3)
                    getArtists(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // remove runnable callback if present
                runnable?.let {
                    handler.removeCallbacks(it)
                }
                // delay artist search
                if (!newText.isNullOrBlank() && newText.length >= 3) {
                    val r = Runnable { getArtists(newText) }
                    runnable = r
                    handler.postDelayed(r, 500)
                }
                return false
            }
        })
    }

    /**
     * Initialize viewModel
     */
    private fun initViewModel() {
        eventViewModel = ViewModelProviders.of(activity!!, EventViewModelFactory())
            .get(EventViewModel::class.java)
    }


    /**
     * Configure publication recyclerView
     */
    private fun configureRecyclerView() {
        fragment_artist_search_recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ArtistRecyclerAdapter(artistList, this)
        fragment_artist_search_recyclerView.adapter = adapter
    }

    /**
     * Hide/Show recycler view
     * @param boolean True = shown, False = hide
     */
    private fun showRecyclerView(boolean: Boolean){
        fragment_artist_search_recyclerView.apply {
            visibility = if (boolean)
                View.VISIBLE
            else
                View.GONE
        }
    }

    /**
     * Hide/Show message
     * @param boolean True = shown, False = hide
     */
    private fun showMessage(boolean: Boolean, message: String? = null){
        fragment_artist_search_message.apply {
            if (boolean) {
                visibility = View.VISIBLE
                text = message ?: ""
            }
            else
                View.GONE
        }
    }

    /**
     * Get artists based on user query search
     * @param query user query string
     */
    private fun getArtists(query: String) {
        eventViewModel.getArtistByName(query, NB_RESULT)
        fragment_artist_search_progress.visibility = View.VISIBLE
    }

    /**
     * Observe artist search result
     */
    private fun observeArtistResults() {
        eventViewModel.artists().observe(viewLifecycleOwner, Observer {
            fragment_artist_search_progress.visibility = View.GONE

            when {
                it.data is List<*> -> {
                    val list: List<Artist> = it.data.filterIsInstance(Artist::class.java)
                    updateUI(ArrayList(list))
                }
                it.error is Int -> Toast
                    .makeText(context, getText(it.error), Toast.LENGTH_SHORT)
                    .show()
                else -> {}
            }
        })
    }

    /**
     * Update recycler view or display message
     * @param artists list of artist corresponding to query search (nullable)
     */
    private fun updateUI(artists: ArrayList<Artist>?){
        artistList.clear()

        if (!artists.isNullOrEmpty()){
            fragment_artist_search_message.visibility = View.GONE
            artistList.addAll(artists)
            // show recycler
            showRecyclerView(true)
            // Hide message
            showMessage(false)
        } else {
            // display message
            val message = getText(R.string.artist_search_no_result)
            showMessage(true, message.toString())
            // Hide Recycler view
            showRecyclerView(false)
        }

        adapter.notifyDataSetChanged()
    }

    /**
     * Recycler item clicked callback
     * @param position item position in adapter
     */
    override fun onItemClicked(position: Int) {
        eventViewModel.selectArtist(adapter.artistList[position])
        findNavController().navigate(R.id.artist_fragment)
        this.dismiss()
    }
}
