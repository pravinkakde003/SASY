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
import com.sasy.nontag.utils.launchAndClearStackActivity
import com.sasy.nontag.utils.pinlock.PinButtonAdapter
import com.sasy.nontag.utils.showToast

class AppPinCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppPincodeBinding
    private var pin = ""
    private var isFromDetailScreen = false

    companion object {
        val CANCELLED = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_pincode)
        binding = ActivityAppPincodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.hasExtra(Constants.IS_FROM_DETAIL_SCREEN)) {
            isFromDetailScreen = intent.getBooleanExtra(Constants.IS_FROM_DETAIL_SCREEN, false)
            binding.categoryLabel.text = resources.getString(R.string.welcome_text_admin)
            binding.categoryImage.setImageResource(R.drawable.ic_admin);
        }
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
        if (isFromDetailScreen) {
            if (getPin() == Constants.ADMIN_PIN) {
                val resultIntent = Intent()
                resultIntent.putExtra(Constants.SELECTED_PIN, getPin())
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                showToast(getString(R.string.please_enter_valid_admin_pin))
            }
        } else {
            if (getPin() == Constants.USER_PIN) {
                launchAndClearStackActivity<DashboardActivity> {}
            } else {
                showToast(getString(R.string.please_enter_valid_user_pin))
            }
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
