package com.example.zaatkotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zaatkotlin.models.User
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var callbackManager: CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()
        var googleSignInButton: SignInButton = findViewById(R.id.googleSignInButton)

        googleSignInButton.setOnClickListener { signInWithGoogle() }

        initSignInWithFacebook()
    }

    private fun initSignInWithFacebook() {
        LoginManager.getInstance().logOut()
        callbackManager = CallbackManager.Factory.create()
        val facebookButton: LoginButton = findViewById(R.id.facebookSignInButton)
        var user: User = User("", "", "", "")
        facebookButton.setReadPermissions("email", "public_profile")
        facebookButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                var request =
                    GraphRequest.newMeRequest(result?.accessToken) { `object`: JSONObject?, response: GraphResponse? ->
                        Log.d("logiiin", `object`.toString())
                        Log.d("login", `object`?.get("name").toString())
                        user = User(
                            `object`?.get("email").toString(),
                            "https://graph.facebook.com/" + `object`?.get("id")
                                .toString() + "/picture?type=large",
                            `object`?.get("name").toString(), ""
                        )
                        firebaseSignInWithFacebook(result?.accessToken, user)
                    }
                val parameters = Bundle()
                parameters.putString("fields", "name,id,email")
                request.parameters = parameters
                request.executeAsync()

            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
            }

        })
    }

    private fun firebaseSignInWithFacebook(
        accessToken: AccessToken?,
        user: User
    ) {
        val credential = accessToken?.token?.let {
            FacebookAuthProvider.getCredential(it)
        }
        if (credential != null) {
            //lambda functions >> add on complete listener take one parameter 'task' and return 'unit'
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "done", Toast.LENGTH_SHORT).show()
                    user.userId = firebaseAuth.uid
                    saveUserDataInFireStore(user)
                } else {
                    Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // :: >> get class reference
                val account =
                    task.getResult(ApiException::class.java)!! // !! throw exception if it equal null

                firebaseSignInWithGoogle(account.idToken!!, account)
            } catch (e: ApiException) {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            }
        }

        callbackManager.onActivityResult(requestCode, resultCode, data) // ?????
    }

    private fun firebaseSignInWithGoogle(
        idToken: String,
        account: GoogleSignInAccount
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "suff", Toast.LENGTH_SHORT).show()
                saveUserDataInFireStore(
                    User(
                        account.email,
                        account.photoUrl.toString(),
                        account.displayName,
                        firebaseAuth.uid
                    )
                )
            } else
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserDataInFireStore(user: User) {
        val db = Firebase.firestore
        user.userId?.let {
            db.collection("Users").document(it).set(user)
        }
    }


}