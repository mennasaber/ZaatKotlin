package com.example.zaatkotlin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zaatkotlin.R
import com.example.zaatkotlin.adapters.SearchAdapter
import com.example.zaatkotlin.models.Follow
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.viewmodels.SearchViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {

    private lateinit var usersList: ArrayList<User>
    private var followList: ArrayList<Follow> = ArrayList()
    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var relativeLayout: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        initWidget(view)
        return view
    }

    /*** ------------------------------ Initialization ----------------------------*/
    private fun initWidget(view: View?) {
        getFollowing()
        val searchImage = view?.findViewById<ImageView>(R.id.searchImage)
        val searchET = view?.findViewById<EditText>(R.id.searchET)
        relativeLayout = view?.findViewById(R.id.progressLayout)!!

        viewModel.usersList.clear()

        usersRecyclerView = view.findViewById(R.id.usersRecyclerView)!!
        searchAdapter = SearchAdapter(viewModel.usersList, viewModel.followList, viewModel)
        usersRecyclerView.adapter = searchAdapter
        usersRecyclerView.layoutManager = LinearLayoutManager(view.context)

        searchImage?.setOnClickListener {
            if (searchET?.text.toString().trim() != "") {
                relativeLayout.visibility = View.VISIBLE
                getUsers(searchET?.text.toString().trim())
            }
        }
        searchET?.addTextChangedListener {
            Log.d("empty", "initWidget: ")
            if (searchET.text.toString().isEmpty()) {
                viewModel.usersList.clear()
                searchAdapter.notifyDataSetChanged()
            }
        }
    }

    /*** ------------------ Get all users that user follow ----------------------------*/
    private fun getFollowing() {
        viewModel.getUserFollowing().observe(viewLifecycleOwner, Observer { querySnapshot ->
            followList.clear()
            if (querySnapshot != null) {
                for (document in querySnapshot)
                    followList.add(document.toObject(Follow::class.java))
            }
        })
    }

    /***--------------- Get users from viewModel that return liveData<QuerySnapShot> --------------*/
    private fun getUsers(searchContent: String) {

        viewModel.getUsersFromDB().observe(viewLifecycleOwner, Observer { querySnapshot ->
            if (querySnapshot != null) {
                viewModel.usersList.clear()
                viewModel.followList.clear()
                for (document in querySnapshot) {
                    if (document.data["username"].toString().toLowerCase(Locale.ROOT).contains(
                            searchContent.toLowerCase(
                                Locale.ROOT
                            )
                        ) && document.data["userId"].toString() != FirebaseAuth.getInstance().uid.toString()
                    ) {

                        val user = User(
                            username = document.data["username"] as String,
                            photoURL = document.data["photoURL"] as String,
                            userId = document.data["userId"] as String,
                            email = document.data["email"] as String
                        )
                        val isFollow = isFollowing(user.userId)
                        viewModel.followList[user.userId!!] = isFollow
                        viewModel.usersList.add(user)
                    }
                }
                searchAdapter.notifyDataSetChanged()
            }
        })
        relativeLayout.visibility = View.GONE
    }

    /*** ------------------ Return true if current user follow this user ------------*/
    private fun isFollowing(userId: String?): Boolean {
        return followList.find { it.followingId == userId } != null
    }
}