package com.example.zaatkotlin.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zaatkotlin.R
import com.example.zaatkotlin.databinding.ActivityLoginBinding
import com.example.zaatkotlin.models.User
import com.example.zaatkotlin.sendNotifications.Token
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        setupWidget(false)
        initSignInWithGoogle()
        initSignInWithFacebook()
    }

    /** ---------------- send request to sign in with google or facebook account ----------**/
    private fun initSignInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val googleSignInButton: SignInButton = findViewById(R.id.googleSignInButton)
        googleSignInButton.setOnClickListener { signInWithGoogle() }
    }

    private fun initSignInWithFacebook() {
        LoginManager.getInstance().logOut()
        callbackManager = CallbackManager.Factory.create()
        val facebookButton: LoginButton = findViewById(R.id.facebookSignInButton)
        var user: User
        facebookButton.setPermissions("email", "public_profile")
        facebookButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val request =
                    GraphRequest.newMeRequest(result?.accessToken) { `object`: JSONObject?, response: GraphResponse? ->
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

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 0)
    }

    /**  --------------------- save user email in firebase auth --------------------**/
    private fun firebaseSignInWithFacebook(
        accessToken: AccessToken?,
        user: User
    ) {
        setupWidget(true)
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
                    login()
                } else {
                    setupWidget(false)
                }
            }
        }
    }

    private fun firebaseSignInWithGoogle(
        idToken: String,
        account: GoogleSignInAccount
    ) {
        setupWidget(true)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                saveUserDataInFireStore(
                    User(
                        account.email,
                        account.photoUrl.toString(),
                        account.displayName,
                        firebaseAuth.uid
                    )
                )
                login()
            } else
                setupWidget(false)
        }
    }

    /**  ------------------------------ manage android widget -----------------------**/
    private fun setupWidget(isLogin: Boolean) {
        if (isLogin) {
            binding.loginProgress.visibility = View.VISIBLE
            binding.loginLayout.visibility = View.INVISIBLE
        } else {
            binding.loginProgress.visibility = View.INVISIBLE
            binding.loginLayout.visibility = View.VISIBLE
        }
    }

    /**  ------------------------------ go to MainActivity --------------------------**/
    private fun login() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /**  ------------------------------ intent callback ------------------------------------**/
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

        callbackManager.onActivityResult(requestCode, resultCode, data) // send data to facebook sdk
    }

    /**  ------------ -----save user data in fireStore ----------------------**/
    private fun saveUserDataInFireStore(user: User) {
        val db = Firebase.firestore
        user.userId?.let {
            db.collection("Users").document(it).set(user)
        }
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("TAG", "getInstanceId failed")
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                val tokenObject = Token(token!!)
                Firebase.firestore.collection("Token").document(user.userId!!)
                    .set(tokenObject)

                Log.d("TAG", "saveUserDataInFireStore: $token")
            })
    }
}