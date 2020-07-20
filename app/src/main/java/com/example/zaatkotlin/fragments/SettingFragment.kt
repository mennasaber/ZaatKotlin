package com.example.zaatkotlin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.LoginActivity
import com.example.zaatkotlin.activities.ProfileActivity
import com.example.zaatkotlin.databinding.FragmentSettingBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class SettingFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        binding.profileLayout.setOnClickListener(this)
        binding.logoutLayout.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.profileLayout -> {
                startActivity(Intent(view.context, ProfileActivity::class.java))
            }
            R.id.logoutLayout -> {
                signOut(view)
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(view.context, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                activity?.finish()
            }
        }
    }

    private fun signOut(view: View?) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = view?.context?.let { GoogleSignIn.getClient(it, gso) }
        mGoogleSignInClient?.signOut()
    }
}