package com.dicoding.picodiploma.storylensapp.view.upload

import com.dicoding.picodiploma.storylensapp.data.pref.dataStore
import android.widget.Toast
import okhttp3.MultipartBody
import androidx.activity.result.contract.ActivityResultContracts
import okhttp3.RequestBody.Companion.asRequestBody
import androidx.activity.result.PickVisualMediaRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.dicoding.picodiploma.storylensapp.data.response.AddResponse
import android.os.Bundle
import android.content.Intent
import com.dicoding.picodiploma.storylensapp.data.api.ApiConfig
import com.dicoding.picodiploma.storylensapp.view.main.MainActivity
import android.net.Uri
import com.dicoding.picodiploma.storylensapp.R
import com.google.gson.Gson
import androidx.lifecycle.lifecycleScope
import okhttp3.RequestBody.Companion.toRequestBody
import com.dicoding.picodiploma.storylensapp.data.pref.UserPreference
import kotlinx.coroutines.Dispatchers
import com.dicoding.picodiploma.storylensapp.databinding.ActivityUploadBinding
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.flow.firstOrNull
import android.util.Log
import android.view.View
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType

class UploadActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_IMAGE_URI = "image_uri"
    }

    private var currentImageUri: Uri? = null
    //init binding activity dan userpreference dan var currentImageUri
    private lateinit var userPreference: UserPreference
    private lateinit var binding: ActivityUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(this.dataStore) // Initialize UserPreference

        //binding button sekaligus memanggil fungsi
        binding.buttonAdd.setOnClickListener { uploadStory() }
        binding.camera.setOnClickListener { startCamera() }
        binding.gallery.setOnClickListener { startGallery() }

        savedInstanceState?.getString(EXTRA_IMAGE_URI)?.let {
            currentImageUri = Uri.parse(it)
            showImage()
        } ?: run {
            binding.image.setImageResource(R.drawable.baseline_image_24)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //menyimpan image uri
        currentImageUri?.let {
            outState.putString(EXTRA_IMAGE_URI, it.toString())
        }
    }

    //fungsi mengambul gambar dari galleri
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.
        PickVisualMedia.ImageOnly))
    }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "Tidak ada media yang dipilih")
        }
    }

    //menggunakan fungsi upload image
    private fun uploadStory() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()
            showLoading(true)

            lifecycleScope.launch {
                val token = withContext(Dispatchers.IO) {
                    userPreference.getSession().firstOrNull()?.token
                }

                token?.let {
                    val requestBody = description.toRequestBody("text/plain".toMediaType())
                    val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                    val multipartBody = MultipartBody.Part.createFormData(
                        "photo",
                        imageFile.name,
                        requestImageFile
                    )
                    try {
                        val apiService = ApiConfig().getApiService(it)
                        val successResponse = apiService.addStory(multipartBody, requestBody)
                        successResponse.message?.let { message ->
                            showToast(message)
                        }
                        // Navigasi ke MainActivity dengan membersihkan task lama
                        val intent = Intent(this@UploadActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)

                    } catch (e: HttpException) {
                        val errorBody = e.response()?.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, AddResponse::class.java)
                        errorResponse.message?.let { errorMessage ->
                            showToast(errorMessage)
                        }
                    } finally {
                        showLoading(false)
                    }
                } ?: showToast(getString(R.string.image_warning))
            }
        } ?: showToast(getString(R.string.image_warning))
    }

    //menampilkan toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    //fungsi menampilkan gambar baik kamera atau galleri
    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.image.setImageURI(it)
        }
    }

    //fungsi mengambil gambar dari galleri
    private fun startCamera() {
        // Panggil fungsi melalui instance
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    //menampilkan loading
    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}