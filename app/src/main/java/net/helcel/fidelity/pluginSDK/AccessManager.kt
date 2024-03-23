package net.helcel.fidelity.pluginSDK

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONException


object AccessManager {
    private const val PREF_KEY_SCOPE = "scope"
    private const val PREF_KEY_TOKEN = "token"

    private fun stringArrayToString(values: ArrayList<String?>): String? {
        if (values.isEmpty()) return null
        val a = JSONArray()
        values.forEach { a.put(it) }
        return a.toString()
    }

    private fun stringToStringArray(s: String?): ArrayList<String> {
        val strings = ArrayList<String>()
        if (s.isNullOrEmpty()) return strings

        try {
            val a = JSONArray(s)
            for (i in 0 until a.length())
                strings.add(a.optString(i))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return strings
    }

    fun storeAccessToken(
        ctx: Context,
        hostPackage: String?,
        accessToken: String?,
        scopes: ArrayList<String?>
    ) {
        val prefs = getPrefsForHost(ctx, hostPackage)
        val edit = prefs.edit()
        edit.putString(PREF_KEY_TOKEN, accessToken)
        val scopesString = stringArrayToString(scopes)
        edit.putString(PREF_KEY_SCOPE, scopesString)
        edit.apply()

        val hostPrefs = ctx.getSharedPreferences("KP2A.PluginAccess.hosts", Context.MODE_PRIVATE)
        if (!hostPrefs.contains(hostPackage))
            hostPrefs.edit().putString(hostPackage, "").apply()
    }

    private fun getPrefsForHost(
        ctx: Context,
        hostPackage: String?
    ): SharedPreferences {
        return ctx.getSharedPreferences("KP2A.PluginAccess.$hostPackage", Context.MODE_PRIVATE)
    }

    fun tryGetAccessToken(ctx: Context, hostPackage: String?, scopes: ArrayList<String?>): String? {
        if (hostPackage.isNullOrEmpty()) return null

        val prefs = getPrefsForHost(ctx, hostPackage)
        val scopesString = prefs.getString(PREF_KEY_SCOPE, "")
        val currentScope = stringToStringArray(scopesString)
        if (!isSubset(scopes, currentScope))
            return null
        return prefs.getString(PREF_KEY_TOKEN, null)

    }

    private fun isSubset(
        requiredScopes: ArrayList<String?>,
        availableScopes: ArrayList<String>
    ): Boolean {
        return availableScopes.containsAll(requiredScopes)
    }

    fun removeAccessToken(
        ctx: Context, hostPackage: String?,
        accessToken: String?
    ) {
        val prefs = getPrefsForHost(ctx, hostPackage)
        if (prefs.getString(PREF_KEY_TOKEN, "") == accessToken) {
            val edit = prefs.edit()
            edit.clear()
            edit.apply()
        }

        val hostPrefs = ctx.getSharedPreferences("KP2A.PluginAccess.hosts", Context.MODE_PRIVATE)
        if (hostPrefs.contains(hostPackage)) {
            hostPrefs.edit().remove(hostPackage).apply()
        }
    }
}
