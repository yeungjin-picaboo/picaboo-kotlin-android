package com.example.picasso

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.picasso.api.WeatherService
import com.example.picasso.databinding.ActivityLoginPageBinding

class LoginPage : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginPageBinding.inflate(layoutInflater)
    }

    var queue: RequestQueue? = null

    private val api = WeatherService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.login.setOnClickListener {
            if (queue == null) {
                queue = Volley.newRequestQueue(this)
            }

            if (login()) {
                postConnection("test", "password")
                //성공시
            } else {
                //실패시
            }
        }

//        binding.login2.setOnClickListener { test(firebaseAuth.currentUser) }
    }


//    fun test(user: FirebaseUser?) {
//        if (user != null) {
//            val userDto = GoogleLoginDto(user.email.toString(), user.uid)
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val response = api.communicate().loginGoogleUser(userDto)
//
//                    if (response.isSuccessful && !response.body().isNullOrEmpty()) {
//                        val sharedPreferences = applicationContext.getSharedPreferences(
//                            "JWT", Context.MODE_PRIVATE
//                        )
//                        val editor = sharedPreferences.edit().also {
//                            it.putString("JWT", response.body().toString())
//                            it.apply() // JWT를 저장
//                        }
//                        startActivity(Intent(this@LoginPage, MainActivity::class.java))
//                        //현재 코드는 MainActivity로 가는거임 나중에 LoginActivity로 가면 됨.
//                        finish()
//                    } else {
//                        Log.d("Error", "Error")
//                    }
//                } catch (err: Error) {
//                    Log.d("Error in toMainActivity", err.toString())
//                }
//            }
//        } else {
//            Toast.makeText(this, "구글 회원가입이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }
//    public override fun onStart() {
//        super.onStart()
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        if (account !== null) { // 이미 로그인 되어있을시 바로 메인 액티비티로 이동
//            toMainActivity(firebaseAuth.currentUser)
//        }
//    } //onStart End
//    fun toMainActivity(user: FirebaseUser?) {
//        if (user != null) { // MainActivity 로 이동
//
//            // 로그인과 동시에 서버로 요청 보내면 됨.
//            val userDto =
//                GoogleRegisterDto(user.email.toString(), user.uid, user.displayName.toString())
//
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val response = api.communicate().registerGoogleUser(userDto)
//
//                    if (response.isSuccessful && response.body() == true) {
//                        startActivity(Intent(this@LoginPage, MainActivity::class.java))
//                        //현재 코드는 MainActivity로 가는거임 나중에 LoginActivity로 가면 됨.
//                        finish()
//                    } else {
//                        Log.d("Error", "Error")
//                    }
//                } catch (err: Error) {
//                    Log.d("Error in toMainActivity", err.toString())
//                }
//            }
//
//
//        }
//    } // toMainActivity End
//
//    // onActivityResult
//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)
//                firebaseAuthWithGoogle(account!!)
//
//            } catch (e: ApiException) {
//                // Google Sign In failed, update UI appropriately
//                Log.w("LoginActivity", "Google sign in failed", e)
//            }
//        }
//    } // onActivityResult End

    // firebaseAuthWithGoogle
//    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
//        Log.d("LoginActivity", "firebaseAuthWithGoogle:" + acct.id!!)
//
//        //Google SignInAccount 객체에서 ID 토큰을 가져와서 Firebase Auth로 교환하고 Firebase에 인증
//        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
//        firebaseAuth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    Log.w("LoginActivity", "firebaseAuthWithGoogle 성공", task.exception)
//                    toMainActivity(firebaseAuth?.currentUser)
//                } else {
//                    Log.w("LoginActivity", "firebaseAuthWithGoogle 실패", task.exception)
////                    Snackbar.make(login_layout, "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show()
//                }
//            }
//    }// firebaseAuthWithGoogle END


    //    private fun signIn() {
//        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
//    }
    // 로그인 함수
    private fun login(): Boolean {
        return true
    }

    private fun postConnection(email: String, password: String) {
        val url: String = "http://10.0.2.2:8080/post"

        val request = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                //dataText.text = "Response: $response"
                Log.d("test", "success")
            }, Response.ErrorListener { error ->
                Log.d("test", "${error}")
            }) {
            // request 시 key, value 보낼 때
            override fun getParams(): Map<String, String> {
                val params = mutableMapOf<String,String>()
                params["email"] = email
                params["password"] = password
                return params
            }

        }

        queue?.add(request)
    }
}