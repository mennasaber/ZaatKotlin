package com.example.zaatkotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var usersList: ArrayList<User>
    private var followList: ArrayList<Follow> = ArrayList()
    private var booleanFollowList: ArrayList<Boolean> = ArrayList()
    lateinit var usersRecyclerView: RecyclerView
    lateinit var searchAdapter: SearchAdapter
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        initWidget(view)
        return view
    }

    // ------------------------------ Initialization ----------------------------
    private fun initWidget(view: View?) {
        getFollowing()
        val searchImage = view?.findViewById<ImageView>(R.id.searchImage)
        val searchET = view?.findViewById<EditText>(R.id.searchET)

        usersList = viewModel.usersList
        booleanFollowList = viewModel.followList

        usersRecyclerView = view?.findViewById(R.id.usersRecyclerView)!!
        searchAdapter = SearchAdapter(usersList, booleanFollowList, viewModel)
        usersRecyclerView.adapter = searchAdapter
        usersRecyclerView.layoutManager = LinearLayoutManager(view.context)

        searchImage?.setOnClickListener {
            if (searchET?.text.toString().trim() != "")
                getUsers(searchET?.text.toString().trim())
        }
    }

    private fun getFollowing() {
        viewModel.getUserFollowing().observe(viewLifecycleOwner, Observer { querySnapshot ->
            followList.clear()
            for (document in querySnapshot)
                followList.add(document.toObject(Follow::class.java))
        })
    }

    // ------------------------------ Get users from viewModel that return liveData<QuerySnapShot> ----------------------------
    private fun getUsers(searchContent: String) {
        viewModel.getUsersFromDB().observe(viewLifecycleOwner, Observer { querySnapshot ->
            usersList.clear()
            booleanFollowList.clear()
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
                    booleanFollowList.add(isFollow)
                    usersList.add(user)
                }
            }
            viewModel.usersList = usersList
            viewModel.followList = booleanFollowList
            searchAdapter.notifyDataSetChanged()
        })
    }

    private fun isFollowing(userId: String?): Boolean {
        return followList.find { it.followingId == userId } != null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}