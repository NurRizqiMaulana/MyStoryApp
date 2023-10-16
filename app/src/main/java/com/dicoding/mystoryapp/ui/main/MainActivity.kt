package com.dicoding.mystoryapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.data.remote.response.ListStoryItem
import com.dicoding.mystoryapp.data.repository.Result
import com.dicoding.mystoryapp.databinding.ActivityMainBinding
import com.dicoding.mystoryapp.helper.ViewModelFactory
import com.dicoding.mystoryapp.ui.adapter.StoryAdapter
import com.dicoding.mystoryapp.ui.addstory.AddStoryActivity
import com.dicoding.mystoryapp.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvUsers.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvUsers.addItemDecoration(itemDecoration)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        binding.fab.setOnClickListener { view ->
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
        setupData()
    }

    private fun setupData() {
        viewModel.getSession().observe(this) { user ->
            if (user.token.isNotBlank()) {
                processGetAllStories(user.token)
            }
        }
    }

    private fun processGetAllStories(token: String) {
        viewModel.getStories(token).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        setListStory(result.data)
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            this,
                            R.string.failed_to_load_data,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setListStory(listStory: List<ListStoryItem>) {
        val adapter = StoryAdapter()
        adapter.submitList(listStory)
        binding.rvUsers.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.action_logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.sign_out)
                    .setMessage(R.string.are_you_sure)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        viewModel.deleteLogin()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }

                val alert = builder.create()
                alert.show()
                true
            }
            else -> true
        }
    }
}