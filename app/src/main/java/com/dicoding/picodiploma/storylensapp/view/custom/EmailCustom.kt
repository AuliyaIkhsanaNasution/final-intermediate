package com.dicoding.picodiploma.storylensapp.view.custom

import androidx.appcompat.widget.AppCompatEditText
import android.view.MotionEvent
import com.dicoding.picodiploma.storylensapp.R
import android.util.AttributeSet
import android.content.Context
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import android.view.View
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable

class EmailCustom @JvmOverloads constructor(

    // bagian constructor yang diperlukan
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle) :
    AppCompatEditText(context, attrs, defStyleAttr), View.OnTouchListener {

    private lateinit var clearButton: Drawable

    // Fungsi untuk mengatur teks hint dan teks alignment
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //meletakkan hint disini sebelum memasukkan teks
        hint = "Masukkan Email Kamu"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    init { initial() }

    // Fungsi untuk mengatur ikon clear button
    private fun clearButtonHide() {
        setDrawables()
    }

    // Fungsi untuk menampilkan ikon clear button
    private fun clearButtonShow() {
        setDrawables(endOfTheText = clearButton)
    }

    //melakukan inisialisasi komponen
    private fun initial() {

        // melihat perubahan yang ada pada text
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            //kemudian disaat text berubah
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = if (!s.contains("@")) {
                    //jika tidak ada @ maka akan menampilkan error
                    resources.getString(R.string.email_error)
                } else {
                    null
                }
                if (s.isNotEmpty()) clearButtonShow() else clearButtonHide()//jika ada maka akan menampilkan clear button
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
        })

        // menampilkan clear button ketika ada perubahan teks
        clearButton =
            ContextCompat.getDrawable(context, R.drawable.baseline_close) as Drawable
        setOnTouchListener(this)
    }

    //set icon drawable
    private fun setDrawables(
        startOfTheText: Drawable? = null, topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null, bottomOfTheText: Drawable? = null
    ) {
        //menambahkan drawable (ikon atau gambar) ke salah satu atau beberapa sisi dari teks
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText, topOfTheText,
            endOfTheText, bottomOfTheText)
    }

    // Fungsi untuk menangani sentuhan (touch) pada clear button
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            val clearButtonStart: Float

            // Mengambil layout direction untuk menentukan posisi clear button
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButton.intrinsicWidth + paddingStart).toFloat()
                if (event.x < clearButtonEnd) isClearButtonClicked = true
            } else {
                clearButtonStart = (width - paddingEnd - clearButton.intrinsicWidth).toFloat()
                if (event.x > clearButtonStart) isClearButtonClicked = true
            }

            //jika clear button di clik
            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButtonShow()
                        return true
                    }
                    //saat ada text maka clear saat touch
                    MotionEvent.ACTION_UP -> {
                        text?.clear()
                        clearButtonHide()
                        return true
                    }
                }
            }
        }
        return false
    }
}
