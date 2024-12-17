package com.dicoding.picodiploma.storylensapp.view.custom

import android.text.TextWatcher
import android.graphics.drawable.Drawable
import android.content.Context
import android.view.View
import com.dicoding.picodiploma.storylensapp.R
import androidx.appcompat.widget.AppCompatEditText
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.text.Editable
import androidx.core.content.ContextCompat

class PasswordCustom @JvmOverloads constructor(
    // bagian constructor yang diperlukan
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle) :
    AppCompatEditText(context, attrs, defStyleAttr),
    View.OnTouchListener {

    //init clear button
    private lateinit var clearButton: Drawable

    //melakukan inisialisasi
    init {initial()}

    // Fungsi untuk mengatur teks hint dan teks alignment
    override fun onDraw(canvas: Canvas) { super.onDraw(canvas)
        hint = "Masukkan Password Kamu"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun initial() {

        // melihat perubahan yang ada pada text
        addTextChangedListener(object : TextWatcher {

            //tidak digunakan
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            //tidak digunakan
            override fun afterTextChanged(s: Editable) {
            }
            //saat ada perubahan
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = if (s.toString().length < 8) {
                    //jika kurang dari 8 maka akan menampilkan error
                    resources.getString(R.string.password_error)
                } else {

                    null
                }
                if (s.isNotEmpty()) clearButtonShow() else clearButtonHide()//jika ada maka akan menampilkan clear button
            }
        })
        clearButton = // menampilkan clear button ketika ada perubahan teks
            ContextCompat.getDrawable(context, R.drawable.baseline_close) as Drawable
        setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            var isClearButtonClicked = false
            val clearButtonEnd: Float

            //jika ada perubahan layout
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

    // Fungsi untuk menampilkan ikon clear button
    private fun clearButtonShow() {
        setButtonDrawables(endOfTheText = clearButton)
    }

    //set icon drawable
    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        //menambahkan drawable (ikon atau gambar) ke salah satu atau beberapa sisi dari teks
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText,
            endOfTheText, bottomOfTheText
        )
    }

    // Fungsi untuk mengatur ikon clear button
    private fun clearButtonHide() {
        setButtonDrawables()
    }
}
