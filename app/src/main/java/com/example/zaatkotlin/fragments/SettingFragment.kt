package com.example.zaatkotlin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.LoginActivity
import com.example.zaatkotlin.activities.ProfileActivity
import com.google.firebase.auth.FirebaseAuth


class SettingFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        val profile = view.findViewById<LinearLayout>(R.id.profileLayout)
        val logout = view.findViewById<LinearLayout>(R.id.logoutLayout)
        profile.setOnClickListener(this)
        logout.setOnClickListener(this)
        return view
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.profileLayout -> {
                startActivity(Intent(view.context, ProfileActivity::class.java))
            }
            R.id.logoutLayout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(view.context, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                activity?.finish()
            }
        }
    }

}