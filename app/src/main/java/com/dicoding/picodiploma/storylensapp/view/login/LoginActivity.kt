package com.dicoding.picodiploma.storylensapp.view.login

import kotlinx.coroutines.launch
import com.dicoding.picodiploma.storylensapp.data.api.ApiConfig
import android.content.Intent
import android.view.WindowInsets
import com.dicoding.picodiploma.storylensapp.data.pref.UserModel
import android.view.View
import com.dicoding.picodiploma.storylensapp.data.pref.UserPreference
import kotlinx.coroutines.withContext
import com.dicoding.picodiploma.storylensapp.view.ViewModelFactory
import com.dicoding.picodiploma.storylensapp.view.signup.SignupActivity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.storylensapp.di.Injection
import kotlinx.coroutines.flow.first
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.os.Bundle
import com.dicoding.picodiploma.storylensapp.data.repository.UserRepository
import android.view.WindowManager
import com.dicoding.picodiploma.storylensapp.databinding.ActivityLoginBinding
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.storylensapp.data.pref.dataStore
import kotlinx.coroutines.Dispatchers
import com.dicoding.picodiploma.storylensapp.view.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var userPreference: UserPreference
    // Inisialisasi ViewBinding
    private lateinit var binding: ActivityLoginBinding
    // Inisialisasi UserRepository
    private lateinit var userRepository: UserRepository
    // Inisialisasi ViewModel
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this, ApiConfig().getApiService("token"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Memanggil fungsi setupView,setupAction, dan playAnimation
        startAnimation()
        viewSet()
        actionSet()

        //instance datastore
        userPreference = UserPreference.getInstance(this.dataStore)
        //ambil userRepo dari injection
        userRepository = Injection.userRepositoryProvide(this, ApiConfig().getApiService("token"))

    }

    //intent ke main activity
    private fun intentMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    // Fungsi untuk mengatur tindakan saat tombol login diklik
    private fun actionSet() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            // Jika email dan password tidak kosong, lakukan login
            if (email.isNotEmpty() && password.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                binding.loginButton.visibility = View.INVISIBLE

                lifecycleScope.launch {
                    try {
                        val response = viewModel.login(email, password)
                        when {
                            response.loginResult != null -> {
                                val user = response.loginResult.token?.let {
                                    UserModel(email, it, true)
                                }
                                if (user != null) {
                                    viewModel.saveSession(user)
                                    withContext(Dispatchers.IO) {
                                        checkUserSession()
                                        intentMainActivity()
                                    }
                                }
                            }
                            // Jika email tidak ditemukan
                            response.message == "User not found" -> {
                                showAlertDialog(
                                    "Daftar Terlebih dahulu",
                                    "Akun dengan email $email belum terdaftar. Silakan daftar terlebih dahulu."
                                ) {
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            SignupActivity::class.java
                                        )
                                    )
                                }
                            }
                            //jika duplikasi email
                            response.message == "Duplicate email" -> {
                                showAlertDialog(
                                    "ups!!",
                                    "Email $email telah terdaftar. Gunakan email lain"
                                )
                            }
                            //selain itu maka dialog kesalahan
                            else -> {
                                showAlertDialog(
                                    "Login Gagal",
                                    response.message ?: "Terjadi kesalahan saat login."
                                )
                            }
                        }
                    } catch (e: Exception) {
                        showAlertDialog(
                            "Login Gagal",
                            "Terjadi kesalahan saat login. Silakan coba lagi."
                        )
                    } finally {
                        binding.loginButton.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    }
                }
            } else {
                showAlertDialog("Login Gagal", "Lengkapi datanya ya.")
            }
        }
    }

    // Fungsi untuk memeriksa sesi pengguna
    private fun checkUserSession() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            val userModel = userPreference.getSession().first()
            // Jika token tidak kosong, periksa apakah pengguna sudah login
            if (userModel.token.isNotEmpty()) {
                if (userModel.isLogin) {
                    intentMainActivity()
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    //tampilkan alert dialog
    private fun showAlertDialog(title: String, message: String, onClick: (() -> Unit)? = null) {
        AlertDialog.Builder(this@LoginActivity).apply {
            //set title,message dan juga button
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                onClick?.invoke()
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    //memulai animasi disini untuk halaman login
    private fun startAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        //binding komponen dan set duration
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(200)
        val emailTextView = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(200)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val passwordTextView = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(200)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(200)

        //menjalankan animasi secara berurutan
        AnimatorSet().apply {
            playSequentially(
                title, message, emailTextView, emailEditTextLayout, passwordTextView, passwordEditTextLayout, login
            )
            startDelay = 100
        }.start()
    }

    // Fungsi untuk mengatur tampilan
    private fun viewSet() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            // Mengatur tampilan untuk fullscreen
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()// Menyembunyikan ActionBar
    }
}