package com.dicoding.mystoryapp.ui.register

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
import com.dicoding.mystoryapp.data.repository.Result
import com.dicoding.mystoryapp.databinding.ActivityRegisterBinding
import com.dicoding.mystoryapp.helper.ViewModelFactory
import com.dicoding.mystoryapp.ui.login.LoginActivity
import com.dicoding.mystoryapp.ui.main.MainViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupButton.setOnClickListener { processRegister() }

        binding.tvSignIn.setOnClickListener { moveLoginActivity() }

        setupView()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun processRegister() {

        binding.apply {
            val name = edRegisterName.text.toString()
            val email = edRegisterEmail.text.toString()
            val password = edRegisterPassword.text.toString()

            viewModel.register(name, email, password).observe(this@RegisterActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                            signupButton.isEnabled = false
                        }
                        is Result.Success -> {
                            showLoading(false)
                            signupButton.isEnabled = true
                            showToast(getString(R.string.create_an_account_success))
                            Toast.makeText(this@RegisterActivity,
                                R.string.create_an_account_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            moveLoginActivity()
                        }
                        is Result.Error -> {
                            showLoading(false)
                            signupButton.isEnabled = true
                            showToast(getString(R.string.create_an_account_failed))
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

    private fun moveLoginActivity(){
        startActivity(Intent(this, LoginActivity::class.java))
    }
}