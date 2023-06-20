package com.sasy.nontag.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import pub.devrel.easypermissions.EasyPermissions

inline fun <reified T : Any> T.className(): String = this::class.java.simpleName

inline fun <reified T : Any> Activity.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
}

inline fun <reified T : Any> Activity.launchAndClearStackActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    intent.clearStack()
    startActivity(intent, options)
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

fun Activity.hasBluetoothPermissions() =
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        EasyPermissions.hasPermissions(
            this,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        EasyPermissions.hasPermissions(
            this,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    }