package io.flatcircle.viewhelper

import android.app.Activity
import android.app.AlertDialog
import android.app.FragmentTransaction
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

/**
 * Created by jacquessmuts on 2019-03-26
 * Convenience Utils for View Classes
 */
@Suppress("unused")
object ViewHelperUtil {

    fun hideSoftKeyboard(view: View?) {
        if (view != null) {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun openSoftKeyboard(view: View?) {
        if (view != null) {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, 0)
        }
    }

    fun getPxFromDp(resources: Resources, dp: Int): Float {
        val dip = dp.toFloat()

        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            resources.displayMetrics
        )
    }

    fun getDpfromPx(resources: Resources, px: Int): Int {
        return Math.round(px / (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun getScreenWidth(view: View): Int {
        return getScreenWidth(view.resources)
    }

    fun getScreenWidth(resources: Resources): Int {
        return resources.displayMetrics.widthPixels
    }

    fun getScreenHeight(view: View): Int {
        return getScreenHeight(view.resources)
    }

    fun getScreenHeight(resources: Resources): Int {
        return resources.displayMetrics.heightPixels
    }

    val View.screenDensity: ScreenDensity?
        get() = calculateScreenDensity(this)

    fun calculateScreenDensity(view: View): ScreenDensity? {
        val activity = getActivityFromView(view)

        if (activity == null)
            return null

        return calculateScreenDensity(activity)
    }

    val Activity.screenDensity: ScreenDensity
        get() = calculateScreenDensity(this)

    fun calculateScreenDensity(activity: Activity): ScreenDensity {

        val metrics = activity.resources.displayMetrics
        activity.windowManager.defaultDisplay.getRealMetrics(metrics)
        val density = metrics.density
        return when {
            density <= DisplayMetrics.DENSITY_280 -> ScreenDensity.LOW
            density <= DisplayMetrics.DENSITY_HIGH -> ScreenDensity.HIGH
            density <= DisplayMetrics.DENSITY_XHIGH -> ScreenDensity.XHIGH
            density <= DisplayMetrics.DENSITY_XXHIGH -> ScreenDensity.XXHIGH
            else -> ScreenDensity.XXXHIGH
        }
    }

    private fun getActivityFromView(v: View?): Activity? {
        if (v == null) {
            return null
        }

        val context = v.context
        return context as? Activity ?: if (context is ContextWrapper) {
            context.baseContext as Activity
        } else {
            null
        }
    }

    /**
     * Recursively finds all children of a given viewgroup and add them as shared elements
     * to fragmentTransaction, if the children have transitionNames.
     * This function can take a full millisecond, so be careful with complex views with many children
     */
    @androidx.annotation.RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun recursivelyAddSharedTransitionsForFragment(parent: ViewGroup, fragmentTransaction: FragmentTransaction) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child is ViewGroup) {
                recursivelyAddSharedTransitionsForFragment(child, fragmentTransaction)
            } else {
                if (child != null && !child.transitionName.isNullOrEmpty()) {
                    try {
                        fragmentTransaction.addSharedElement(child, child.transitionName)
                    } catch (e: IllegalArgumentException) {
                        Log.w("ViewHelperUtil","$e\nThere is more than one element in this screen with the same transitionName of ${child.transitionName}")
                    }
                }
            }
        }
    }

    fun showShareDialog(
        activity: Activity,
        message: String,
        subject: String,
        requestCodeRequiredForResult: Int? = null,
        email: String = ""
    ) {
        val intent = buildShareDialogIntent(activity, message, subject, email)

        if (requestCodeRequiredForResult != null) {
            activity.startActivityForResult(intent, requestCodeRequiredForResult)
        } else {
            activity.startActivity(intent)
        }
    }

    private fun buildShareDialogIntent(activity: Activity, message: String, subject: String, email: String, @StringRes headerResId: Int? = R.string.send_to): Intent {
        val sendIntent = if (email.isEmpty()) {
            Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
            }
        } else {
            Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", email, null)).apply {
                putExtra(Intent.EXTRA_EMAIL, email)
            }
        }

        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        return Intent.createChooser(sendIntent, activity.resources.getText(R.string.send_to))
    }

    fun showEmailDialog(activity: Activity, destination: String, subject: String, msg: String, requestCodeRequiredForResult: Int? = null) {
        var intent = buildEmailIntent(destination, subject, msg)
        if (intent.resolveActivity(activity.getPackageManager()) == null) {
            intent = buildShareDialogIntent(activity, msg, subject, destination)
        }
        if (requestCodeRequiredForResult != null) {
            activity.startActivityForResult(intent, requestCodeRequiredForResult)
        } else {
            activity.startActivity(intent)
        }
    }

    private fun buildEmailIntent(destination: String, subject: String, msg: String): Intent {
        val intent = Intent(Intent.ACTION_SENDTO)
        val uriText = "mailto:$destination" +
            "?subject=${Uri.encode(subject)}" +
            "&body=${Uri.encode(msg)}"

        val uri = Uri.parse(uriText)
        intent.setData(uri)
        return intent
    }

    fun showConfirmation(
        context: Context,
        titleText: Int,
        bodyText: Int,
        positiveTextResId: Int,
        negativeTextResId: Int? = null,
        result: (confirmed: Boolean) -> Unit
    ) {

        val negativeText = if (negativeTextResId == null) {
            null
        } else {
            context.getString(negativeTextResId)
        }

        showConfirmation(context,
            context.getString(titleText),
            context.getString(bodyText),
            context.getString(positiveTextResId),
            negativeText,
            result)
    }

    fun showConfirmation(
        context: Context,
        titleText: String,
        bodyText: String,
        positiveText: String,
        negativeText: String?,
        result: (confirmed: Boolean) -> Unit
    ) {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
        } else {
            AlertDialog.Builder(context)
        }

        val dialogBuilder = builder.setTitle(titleText)
            .setMessage(bodyText)
            .setCancelable(true)
            .setPositiveButton(positiveText, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    result(true)
                }
            })

        val dialog = if (negativeText != null) {
            dialogBuilder.setNegativeButton(negativeText, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    result(false)
                } })
                .show()
        } else {
            dialogBuilder.show()
        }

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.error_red))
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.prussian_blue))
    }

}