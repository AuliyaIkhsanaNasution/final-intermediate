package com.dicoding.picodiploma.storylensapp.view.signup

import android.os.Build
import com.dicoding.picodiploma.storylensapp.data.repository.UserRepository
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.appcompat.app.AlertDialog
import android.view.View
import com.dicoding.picodiploma.storylensapp.data.api.ApiConfig
import com.dicoding.picodiploma.storylensapp.di.Injection
import android.content.Intent
import kotlinx.coroutines.launch
import android.view.WindowInsets
import com.dicoding.picodiploma.storylensapp.databinding.ActivitySignupBinding
import androidx.activity.viewModels
import android.animation.AnimatorSet
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.storylensapp.view.ViewModelFactory
import android.animation.ObjectAnimator
import com.dicoding.picodiploma.storylensapp.view.login.LoginActivity
import android.view.WindowManager

class SignupActivity : AppCompatActivity() {

    //init user repository, viewmodel
    private lateinit var userRepository: UserRepository
    private val viewModel by viewModels<SignupViewModel>
    { ViewModelFactory.getInstance(this, ApiConfig().getApiService("")) }

    //init binding activity
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //pemanggilan fungsi fungsi yang digunakan
        startAnimation()
        viewSet()
        actionSet()
        liveDataObserve()

        //provideuserrepository
        userRepository =
            Injection.userRepositoryProvide(this, ApiConfig().getApiService("token"))
    }

    //fungsi untuk melakukan animasi
    private fun startAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            //memulai animasi
        }.start()

        //memberikan set duration dan alpha
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
        val nameTextView = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(200)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val emailTextView = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(200)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val passwordTextView = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(200)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(200)

        //menjalankan animasi secara berurutan
        AnimatorSet().apply {
            playSequentially(
                title, nameTextView, nameEditTextLayout, emailTextView, emailEditTextLayout, passwordTextView, passwordEditTextLayout, signup
            )
            startDelay = 100
        }.start()
    }


    // Fungsi untuk mengatur tindakan saat tombol register diklik
    private fun actionSet() {
        binding.signupButton.setOnClickListener {
            //binding komponen
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    viewModel.register(name, email, password)
                }
            }
        }
    }

    //mengeset view
    private fun viewSet() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                //menjadikan layar menjadi fullscreen
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()//sembunyilan action bar
    }


    private fun liveDataObserve() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.signupButton.visibility = View.INVISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.signupButton.visibility = View.VISIBLE
            }
        }
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                showDialog("Error", errorMessage, true)
            }
        }
        viewModel.registerResponse.observe(this) { registerResponse ->
            if (registerResponse != null) {
                showDialog("Success", registerResponse.message ?: "Registration successful", false)
            }
        }
    }

    //menampilkan show dialog
    private fun showDialog(title: String, message: String, isError: Boolean) {
        if (!isFinishing) { // Memastikan aktivitas belum dihancurkan
            AlertDialog.Builder(this@SignupActivity).apply {
                //set judul,pesan dan juga button
                setTitle(title)
                setMessage(message)
                setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    if (!isError) {
                        val intent = Intent(this@SignupActivity,
                            LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                create()
                show()
            }
        }
    }
}