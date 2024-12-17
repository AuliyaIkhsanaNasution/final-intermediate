package com.dicoding.picodiploma.storylensapp.view.welcome

import android.view.WindowManager
import android.os.Bundle
import com.dicoding.picodiploma.storylensapp.view.signup.SignupActivity
import android.view.View
import android.view.WindowInsets
import android.animation.ObjectAnimator
import com.dicoding.picodiploma.storylensapp.view.login.LoginActivity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.storylensapp.databinding.ActivityWelcomeBinding
import android.animation.AnimatorSet

class WelcomeActivity : AppCompatActivity() {
    //init viebinding
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //panggil fungsi fungsi
        setupAction()
        setupView()
        startAnimation()
    }

    private fun setupAction() {
        //fungsi button ketika diklik
        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
    //fungsi animasi
    private fun startAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        //set duration
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val desc = ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).setDuration(100)
        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }
        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }
    }

    //melakukan fungsi setupview
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            //membuat layar fullscreen
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()//agar toolbar tidak muncul
    }
}