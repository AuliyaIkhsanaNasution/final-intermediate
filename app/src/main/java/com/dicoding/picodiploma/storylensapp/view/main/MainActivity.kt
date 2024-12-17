package com.dicoding.picodiploma.storylensapp.view.main

import com.dicoding.picodiploma.storylensapp.view.ViewModelFactory
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.dicoding.picodiploma.storylensapp.view.maps.MapsActivity
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.storylensapp.R
import com.dicoding.picodiploma.storylensapp.databinding.ActivityMainBinding
import com.dicoding.picodiploma.storylensapp.data.api.ApiConfig
import android.os.Bundle
import androidx.activity.viewModels
import com.dicoding.picodiploma.storylensapp.view.upload.UploadActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.storylensapp.view.welcome.WelcomeActivity
import android.content.Intent
import android.view.Menu

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this, ApiConfig().getApiService("token"))
    }
    // Inisialisasi ViewBinding
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainPagingAdapter: MainPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inisialisasi MainAdapter
        mainPagingAdapter = MainPagingAdapter()

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin || user.token.isEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        //submit paging
        viewModel.getStoryPager.observe(this) {
            mainPagingAdapter.submitData(lifecycle, it)
        }

        // Mengamati perubahan pada errorMessage
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        // Mengamati perubahan pada isLoading
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        setRecyclerView()
    }

    //fungsi untuk menampilkan recyclerview
    private fun setRecyclerView() {
        binding.rvStory.layoutManager = LinearLayoutManager(this)

        // Tambahkan footer menggunakan LoadStateAdapter
        binding.rvStory.adapter = mainPagingAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter { mainPagingAdapter.retry() }
        )
    }

    //fungsi untuk menampilkan option menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return true
    }
    //fungsi untuk mengatur action pada menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            //action add untuk ke halaman upload
            R.id.add -> {
                startActivity(Intent(this@MainActivity,
                    UploadActivity::class.java))
                true
            }

            R.id.action_maps -> {
                startActivity(Intent(this@MainActivity,
                    MapsActivity::class.java))
                true
            }

            R.id.action_logout -> {
                // Panggil fungsi logout di ViewModel
                viewModel.logout()

                // Pindah ke LoginActivity atau hapus data sesi
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}