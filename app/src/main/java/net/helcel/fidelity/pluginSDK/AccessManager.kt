package net.helcel.fidelity.pluginSDK

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import org.json.JSONArray
import org.json.JSONException

object AccessManager {
    private const val _tag = "Kp2aPluginSDK"
    private const val PREF_KEY_SCOPE = "scope"
    private const val PREF_KEY_TOKEN = "token"

    private fun stringArrayToString(values: ArrayList<String?>): String? {
        val a = JSONArray()
        for (i in values.indices) {
            a.put(values[i])
        }
        return if (values.isNotEmpty()) {
            a.toString()
        } else {
            null
        }
    }

    private fun stringToStringArray(s: String?): ArrayList<String> {
        val strings = ArrayList<String>()
        if (!TextUtils.isEmpty(s)) {
            try {
                val a = JSONArray(s)
                for (i in 0 until a.length()) {
                    val url = a.optString(i)
                    strings.add(url)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return strings
    }

    fun storeAccessToken(
        ctx: Context,
        hostPackage: String,
        accessToken: String,
        scopes: ArrayList<String?>
    ) {
        val prefs = getPrefsForHost(ctx, hostPackage)

        val edit = prefs.edit()
        edit.putString(PREF_KEY_TOKEN, accessToken)
        val scopesString = stringArrayToString(scopes)
        edit.putString(PREF_KEY_SCOPE, scopesString)
        edit.apply()
        Log.d(
            _tag,
            "stored access token " + accessToken.substring(
                0,
                4
            ) + "... for " + scopes.size + " scopes (" + scopesString + ")."
        )

        val hostPrefs = ctx.getSharedPreferences("KP2A.PluginAccess.hosts", Context.MODE_PRIVATE)
        if (!hostPrefs.contains(hostPackage)) {
            hostPrefs.edit().putString(hostPackage, "").apply()
        }
    }

    fun preparePopup(popupMenu: Any) {
        try {
            val fields = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field[popupMenu]
                    val classPopupHelper = Class.forName(
                        menuPopupHelper
                            .javaClass.name
                    )
                    val setForceIcons = classPopupHelper.getMethod(
                        "setForceShowIcon", Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getPrefsForHost(
        ctx: Context,
        hostPackage: String
    ): SharedPreferences {
        val prefs = ctx.getSharedPreferences("KP2A.PluginAccess.$hostPackage", Context.MODE_PRIVATE)
        return prefs
    }

    fun tryGetAccessToken(ctx: Context, hostPackage: String, scopes: ArrayList<String?>): String? {
        if (TextUtils.isEmpty(hostPackage)) {
            Log.d(_tag, "hostPackage is empty!")
            return null
        }
        Log.d(_tag, "trying to find prefs for $hostPackage")
        val prefs = getPrefsForHost(ctx, hostPackage)
        val scopesString = prefs.getString(PREF_KEY_SCOPE, "")
        Log.d(_tag, "available scopes: $scopesString")
        val currentScope = stringToStringArray(scopesString)
        if (isSubset(scopes, currentScope)) {
            return prefs.getString(PREF_KEY_TOKEN, null)
        } else {
            Log.d(_tag, "looks like scope changed. Access token invalid.")
            return null
        }
    }

    private fun isSubset(
        requiredScopes: ArrayList<String?>,
        availableScopes: ArrayList<String>
    ): Boolean {
        for (r in requiredScopes) {
            if (availableScopes.indexOf(r) < 0) {
                Log.d(_tag, "Scope " + r + " not available. " + availableScopes.size)
                return false
            }
        }
        return true
    }

    fun removeAccessToken(
        ctx: Context, hostPackage: String,
        accessToken: String
    ) {
        val prefs = getPrefsForHost(ctx, hostPackage)

        Log.d(_tag, "removing AccessToken.")
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

    fun getAllHostPackages(ctx: Context): Set<String> {
        val prefs = ctx.getSharedPreferences("KP2A.PluginAccess.hosts", Context.MODE_PRIVATE)
        val result: MutableSet<String> = HashSet()
        for (host in prefs.all.keys) {
            try {
                val info = ctx.packageManager.getPackageInfo(host, PackageManager.GET_META_DATA)
                //if we get here, the package is still there
                result.add(host)
            } catch (e: PackageManager.NameNotFoundException) {
                //host gone. ignore.
            }
        }
        return result
    }


    /**
     * Returns a valid access token or throws PluginAccessException
     */
    fun getAccessToken(
        context: Context, hostPackage: String,
        scopes: ArrayList<String?>
    ): String {
        val accessToken = tryGetAccessToken(context, hostPackage, scopes)
            ?: throw PluginAccessException(hostPackage, scopes)
        return accessToken
    }
}
