package com.dicoding.mystoryapp.ui.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.data.local.datastore.UserModel
import com.dicoding.mystoryapp.data.repository.Result
import com.dicoding.mystoryapp.databinding.ActivityLoginBinding
import com.dicoding.mystoryapp.helper.ViewModelFactory
import com.dicoding.mystoryapp.ui.main.MainActivity
import com.dicoding.mystoryapp.ui.main.MainViewModel
import com.dicoding.mystoryapp.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signinButton.setOnClickListener { processLogin() }

        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        setupView()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(
            binding.imageView,
            View.TRANSLATION_X,
            TRANSLATION_X_START,
            TRANSLATION_X_END
        ).apply {
            duration = ANIMATION_DURATION
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun processLogin() {
        binding.apply {
            val email = edLoginEmail.text.toString()
            val password = edLoginPassword.text.toString()
            viewModel.login(email, password).observe(this@LoginActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                            signinButton.isEnabled = false
                        }

                        is Result.Success -> {
                            showLoading(false)
                            signinButton.isEnabled = true
                            viewModel.setlogin(
                                UserModel(
                                    email,
                                    result.data.loginResult.name,
                                    result.data.loginResult.token
                                )
                            )
                            showToast(getString(R.string.sign_in_success))
                            moveToMainActivity()

                        }

                        is Result.Error -> {
                            showLoading(false)
                            signinButton.isEnabled = true
                            showToast(getString(R.string.sign_in_failed))
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private companion object {
        private const val TRANSLATION_X_START = -30f
        private const val TRANSLATION_X_END = 30f
        private const val ANIMATION_DURATION = 6000L
    }
}