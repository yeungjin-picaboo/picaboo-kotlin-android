package com.example.picasso

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.picasso.databinding.ActivityMainBinding
import com.example.picasso.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class signInActivity : AppCompatActivity(),View.OnClickListener {

    // FireBase인증을 위한 변수
    var auth : FirebaseAuth ?= null

    // 구글 로그인 연동에 필요한 변수
    var googleSignInClient : GoogleSignInClient ?= null
    var GOOGLE_LOGIN_CODE = 9001

    private val binding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // firebaseauth를 사용하기 위한 인스턴스 get
        auth = FirebaseAuth.getInstance()

        // 구글 로그인 버튼 가져오기
        var google_sign_in_button = binding.googleSignInButton

        google_sign_in_button.setOnClickListener(this)

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
    }

    override fun onClick(v: View?) {
        var signInIntent = googleSignInClient?.signInIntent
        if (signInIntent != null) {
            startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                if(result.isSuccess) {
                    var account = result.signInAccount
                    firebaseAuthWithGoogle(account)
                }
            }
        } //if
    } // onActivityResult

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful) {
                    // 로그인 성공 시
                    Toast.makeText(this,  "success", Toast.LENGTH_LONG).show()
                    startActivity(Intent (this, MainActivity::class.java))
                } else {
                    // 로그인 실패 시
                    Toast.makeText(this,  task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    } //firebaseAuthWithGoogle
}