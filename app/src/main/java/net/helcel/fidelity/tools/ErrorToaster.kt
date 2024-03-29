package net.helcel.fidelity.tools

import android.content.Context
import android.widget.Toast

object ErrorToaster {
    private fun helper(activity: Context?, message: String, length: Int) {
        if (activity != null)
            Toast.makeText(activity, message, length).show()
    }

    fun noKP2AFound(activity: Context?) {
        helper(activity, "KeePass2Android Not Installed", Toast.LENGTH_LONG)
    }

    fun formIncomplete(activity: Context?) {
        helper(activity, "Form Incomplete", Toast.LENGTH_SHORT)
    }

    fun invalidFormat(activity: Context?) {
        helper(activity, "Invalid Format", Toast.LENGTH_SHORT)
    }

    fun nothingFound(activity: Context?) {
        helper(activity, "Nothing Found", Toast.LENGTH_SHORT)
    }

    fun noPermission(activity: Context?) {
        helper(activity, "Missing Permission", Toast.LENGTH_LONG)
    }
}
