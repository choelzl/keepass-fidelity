package net.helcel.fidelity.tools

import android.app.Activity
import android.widget.Toast

object ErrorToaster {
    private fun helper(activity: Activity, message: String, length: Int) {
        Toast.makeText(activity, message, length).show()
    }

    fun noKP2AFound(activity: Activity) {
        helper(activity, "KeePass2Android Not Installed", Toast.LENGTH_LONG)
    }

    fun formIncomplete(activity: Activity) {
        helper(activity, "Form Incomplete", Toast.LENGTH_SHORT)
    }

    fun invalidFormat(activity: Activity) {
        helper(activity, "Invalid Format", Toast.LENGTH_SHORT)
    }
}
