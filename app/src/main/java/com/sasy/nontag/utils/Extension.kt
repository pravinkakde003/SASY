package com.sasy.nontag.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.sasy.nontag.R

inline fun <reified T : Any> T.className(): String = this::class.java.simpleName

inline fun <reified T : Any> Activity.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
}

inline fun <reified T : Any> Activity.launchAndClearStackActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    intent.clearStack()
    startActivity(intent, options)
    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
}

inline fun <reified T : Any> newIntent(context: Context): Intent = Intent(context, T::class.java)

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Extension function to check if the [View] is visible
 * */

fun View.show() {
    this.visibility = View.VISIBLE
}

/**
 * Extension function to check if the [View] is hidden
 * */

fun View.hide() {
    this.visibility = View.GONE
}

/**
 * Extension function to check if the [View] is invisible
 * */
fun View.invisible() {
    this.visibility = View.INVISIBLE
}

/**
 * Extension function to enable View
 * */
fun View.enable() {
    this.isEnabled = true
}

/**
 * Extension function to disable View
 * */
fun View.disable() {
    this.isEnabled = false
}

fun <T> Fragment.showToast(obj: T) {
    val toast = Toast.makeText(this.requireContext(), obj.toString(), Toast.LENGTH_SHORT)
    toast?.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}

/**
 * Clear previous activities in stack
 * */
fun Intent.clearStack() {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
}

fun <T> Context.openWebLink(link: T) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link.toString()))
    startActivity(browserIntent)
}

fun Activity.hideKeyBoard() {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(
        currentFocus?.windowToken,
        0
    )
}

fun Activity.showKeyBoard(editable: EditText) {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(
        editable,
        0
    )
}

fun dp2px(context: Context, dp: Float): Float {
    return dp * context.resources.displayMetrics.density
}

fun px2dp(context: Context, px: Float): Float {
    return px / context.resources.displayMetrics.density
}

fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    addToBackStack: Boolean = false,
    container: Int = R.id.containerFragment
) {
    val transaction = supportFragmentManager.beginTransaction()
    if (addToBackStack) {
        transaction.replace(container, fragment).addToBackStack(fragment.tag).commit()
    } else {
        transaction.replace(container, fragment).commit()
    }
}

fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)

fun Activity.isBluetoothAvailable(): Boolean {
    packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH) }?.also {
        return false
    }
    return true
}

fun ImageView.setTint(@ColorRes colorRes: Int) {
    ImageViewCompat.setImageTintList(
        this,
        ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
    )
}

/** @return true if the user already has Bluetooth connect permission */
fun Context.hasBluetoothPermission() = getBluetoothPermissions().isEmpty()

/**
 * Bluetooth permissions (or empty if we already have what we need)
 */
fun Context.getBluetoothPermissions(): Array<String> {
    val perms = mutableListOf<String>()

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        perms.add(Manifest.permission.BLUETOOTH_SCAN)
        perms.add(Manifest.permission.BLUETOOTH_CONNECT)
    } else if (!hasCompanionDeviceApi()) {
        perms.add(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    return getMissingPermissions(perms)
}

/**
 * @return true if CompanionDeviceManager API is present
 */
fun Context.hasCompanionDeviceApi(): Boolean =
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        packageManager.hasSystemFeature(PackageManager.FEATURE_COMPANION_DEVICE_SETUP)
    else false

/**
 * return a list of the permissions we don't have
 */
fun Context.getMissingPermissions(perms: List<String>): Array<String> = perms.filter {
    ContextCompat.checkSelfPermission(
        this,
        it
    ) != PackageManager.PERMISSION_GRANTED
}.toTypedArray()

/**
 * return the text string of the permissions missing
 */
val Context.permissionMissing: String
    get() = if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) {
        getString(R.string.permission_missing)
    } else {
        getString(R.string.permission_missing_31)
    }

fun Context.hasPermissions() = Constants.REQUIRED_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}