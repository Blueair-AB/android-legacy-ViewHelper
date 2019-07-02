@file:Suppress("unused")

package io.flatcircle.viewhelper

import android.view.View

/**
 * Created by jacquessmuts on 2019-04-30
 * Extension classes for Views
 */

fun setNameSir() {
//    val context = Context()
//    val textView = TextView(context)
//    textView.setText(R.string.app_name)

}

class MaInActivity {

}

fun View.hide() {
    this.show(false)
}

fun View.show(shouldShow: Boolean = true) {

    this.visibility = if (shouldShow) {
        View.VISIBLE
    } else {
        View.GONE
    }
}
