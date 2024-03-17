package net.helcel.fidelity.pluginSDK

import android.content.Intent
import android.text.TextUtils
import org.json.JSONException
import org.json.JSONObject

object Kp2aControl {
    /**
     * Creates and returns an intent to launch Keepass2Android for adding an entry with the given fields.
     * @param fields Key/Value pairs of the field values. See KeepassDefs for standard keys.
     * @param protectedFields List of keys of the protected fields.
     * @return Intent to start Keepass2Android.
     * @throws JSONException
     */
    fun getAddEntryIntent(
        fields: HashMap<String?, String?>?,
        protectedFields: ArrayList<String?>?
    ): Intent {
        return getAddEntryIntent(JSONObject((fields as Map<*, *>?)!!).toString(), protectedFields)
    }

    private fun getAddEntryIntent(
        outputData: String?,
        protectedFields: ArrayList<String?>?
    ): Intent {
        val startKp2aIntent = Intent(Strings.ACTION_START_WITH_TASK)
        startKp2aIntent.addCategory(Intent.CATEGORY_DEFAULT)
        startKp2aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startKp2aIntent.putExtra("KP2A_APPTASK", "CreateEntryThenCloseTask")
        startKp2aIntent.putExtra("ShowUserNotifications", "false") //KP2A expects a StringExtra
        startKp2aIntent.putExtra(Strings.EXTRA_ENTRY_OUTPUT_DATA, outputData)
        if (protectedFields != null) startKp2aIntent.putStringArrayListExtra(
            Strings.EXTRA_PROTECTED_FIELDS_LIST,
            protectedFields
        )


        return startKp2aIntent
    }


    /**
     * Creates an intent to open a Password Entry matching searchText
     * @param searchText queryString
     * @param showUserNotifications if true, the notifications (copy to clipboard, keyboard) are displayed
     * @param closeAfterOpen if true, the entry is opened and KP2A is immediately closed
     * @return Intent to start KP2A with
     */
    fun getOpenEntryIntent(
        searchText: String?,
        showUserNotifications: Boolean,
        closeAfterOpen: Boolean
    ): Intent {
        val startKp2aIntent = Intent(Strings.ACTION_START_WITH_TASK)
        startKp2aIntent.addCategory(Intent.CATEGORY_DEFAULT)
        startKp2aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startKp2aIntent.putExtra("KP2A_APPTASK", "SearchUrlTask")
        startKp2aIntent.putExtra("ShowUserNotifications", showUserNotifications.toString())
        startKp2aIntent.putExtra("CloseAfterCreate", closeAfterOpen.toString())
        startKp2aIntent.putExtra("UrlToSearch", searchText)
        return startKp2aIntent
    }

    /**
     * Creates an intent to query a password entry from KP2A. The credentials are returned as Activity result.
     * @param searchText Text to search for. Should be a URL or "androidapp://com.my.package."
     * @return an Intent to start KP2A with
     */
    fun getQueryEntryIntent(searchText: String?): Intent {
        val i = Intent(Strings.ACTION_QUERY_CREDENTIALS)
        if (!TextUtils.isEmpty(searchText)) i.putExtra(Strings.EXTRA_QUERY_STRING, searchText)
        return i
    }

    val queryEntryIntentForOwnPackage: Intent
        /**
         * Creates an intent to query a password entry from KP2A, matching to the current app's package .
         * The credentials are returned as Activity result.
         * This requires SCOPE_QUERY_CREDENTIALS_FOR_OWN_PACKAGE.
         * @return an Intent to start KP2A with
         */
        get() = Intent(Strings.ACTION_QUERY_CREDENTIALS_FOR_OWN_PACKAGE)

    /**
     * Converts the entry fields returned in an intent from a query to a hashmap.
     * @param intent data received in onActivityResult after getQueryEntryIntent(ForOwnPackage)
     * @return HashMap with keys = field names (see KeepassDefs for standard keys) and values = values
     */
    fun getEntryFieldsFromIntent(intent: Intent): HashMap<String, String> {
        val res = HashMap<String, String>()
        try {
            val json = JSONObject(intent.getStringExtra(Strings.EXTRA_ENTRY_OUTPUT_DATA)!!)
            val iter = json.keys()
            while (iter.hasNext()) {
                val key = iter.next()
                val value = json[key].toString()
                res[key] = value
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return res
    }
}
