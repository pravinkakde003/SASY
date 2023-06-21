package com.sasy.nontag.ui

import android.os.Bundle
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.sasy.nontag.R
import com.sasy.nontag.utils.dp2px
import com.sasy.nontag.utils.launchActivity
import com.sasy.nontag.utils.pinlock.PinButtonAdapter
import com.sasy.nontag.utils.showToast
import kotlinx.android.synthetic.main.activity_app_pincode.*

class AppPinCodeActivity : AppCompatActivity() {
    private var pin = ""

    companion object {
        val CANCELLED = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_pincode)
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView)

        windowInsetsController?.isAppearanceLightNavigationBars = true
        gridView.adapter = PinButtonAdapter(this)
        gridView.numColumns = 3

        cancelButton.setOnClickListener {
            onCancelButtonPressed()
        }

        reloadPinView()
    }

    private fun reloadPinView() {
        val dotSize = dp2px(this, 20.0f).toInt()
        val dotMargin = dp2px(this, 10.0f).toInt()
        pinView.removeAllViews()
        (1..getMaxPinSize()).forEach {
            val imageView = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(dotSize, dotSize, 0.0f)
            layoutParams.setMargins(dotMargin, dotMargin, dotMargin, dotMargin)
            imageView.layoutParams = layoutParams
            if (it > pin.length) {
                imageView.setImageResource(R.drawable.dot_empty)
            } else {
                imageView.setImageResource(R.drawable.dot_fill)
            }
            pinView.addView(imageView)
        }
    }

    private fun getMaxPinSize(): Int {
        return 4
    }

    fun onPinButtonClicked(text: String) {
        if (pin.length < getMaxPinSize()) {
            this.pin += text
            reloadPinView()
        }

        if (pin.length == getMaxPinSize()) {
            onPinInputFinished()
        }
    }

    private fun onPinInputFinished() {
        showToast("Finished : " + getPin())
        launchActivity<DashboardActivity> {}
    }

    /*
     * Delete one character.
     */
    open fun onDeleteButtonClicked() {
        if (pin.isNotEmpty()) {
            pin = pin.substring(0, pin.length - 1)
            reloadPinView()
        }
    }

    fun getPin(): String {
        return pin
    }

    fun clearPin() {
        pin = ""
        reloadPinView()
    }

    /*
     * Called when "CANCEL" button is pressed.
     */
    private fun onCancelButtonPressed(): Boolean {
        setResult(CANCELLED)
        finish()
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (event != null && keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (pin.isNotEmpty()) {
                onDeleteButtonClicked()
            } else {
                setResult(CANCELLED)
                finish()
            }
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }
}
