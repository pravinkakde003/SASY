package com.sasy.nontag.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.sasy.nontag.R
import com.sasy.nontag.databinding.ActivityAppPincodeBinding
import com.sasy.nontag.utils.Constants
import com.sasy.nontag.utils.dp2px
import com.sasy.nontag.utils.pinlock.PinButtonAdapter
import com.sasy.nontag.utils.showToast

class AppPinCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppPincodeBinding
    private var pin = ""

    companion object {
        val CANCELLED = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_pincode)
        binding = ActivityAppPincodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView)

        windowInsetsController?.isAppearanceLightNavigationBars = true
        binding.gridView.adapter = PinButtonAdapter(this)
        binding.gridView.numColumns = 3

        binding.cancelButton.setOnClickListener {
            onCancelButtonPressed()
        }

        reloadPinView()
    }

    private fun reloadPinView() {
        val dotSize = dp2px(this, 20.0f).toInt()
        val dotMargin = dp2px(this, 10.0f).toInt()
        binding.pinView.removeAllViews()
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
            binding.pinView.addView(imageView)
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
        if (getPin() == Constants.ADMIN_PIN || getPin() == Constants.USER_PIN) {
            val resultIntent = Intent()
            resultIntent.putExtra(Constants.SELECTED_PIN, getPin())
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        } else {
            showToast(getString(R.string.please_enter_valid_pin))
            clearPin()
        }
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

    private fun getPin(): String {
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
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
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
