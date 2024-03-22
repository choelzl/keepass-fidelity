package net.helcel.fidelity.pluginSDK

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PluginActionBroadcastReceiver : BroadcastReceiver() {
    open class PluginActionBase
        (var context: Context, protected var _intent: Intent) {
        val hostPackage: String?
            get() = _intent.getStringExtra(Strings.EXTRA_SENDER)
    }

    open class PluginEntryActionBase(context: Context, intent: Intent) :
        PluginActionBase(context, intent) {
        protected val entryFieldsFromIntent: HashMap<String, String>
            get() {
                val res = HashMap<String, String>()
                try {
                    val json =
                        JSONObject(_intent.getStringExtra(Strings.EXTRA_ENTRY_OUTPUT_DATA) ?: "")
                    val iter = json.keys()
                    while (iter.hasNext()) {
                        val key = iter.next()
                        val value = json[key].toString()
                        res[key] = value
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                return res
            }

        protected val protectedFieldsListFromIntent: Array<String?>?
            get() {
                try {
                    val json =
                        JSONArray(_intent.getStringExtra(Strings.EXTRA_PROTECTED_FIELDS_LIST))
                    val res = arrayOfNulls<String>(json.length())
                    for (i in 0 until json.length()) res[i] = json.getString(i)
                    return res
                } catch (e: JSONException) {
                    e.printStackTrace()
                    return null
                }
            }


        open val entryId: String?
            get() = _intent.getStringExtra(Strings.EXTRA_ENTRY_ID)


        fun setEntryField(fieldId: String?, fieldValue: String?, isProtected: Boolean) {
            val i = Intent(Strings.ACTION_SET_ENTRY_FIELD)
            val scope = ArrayList<String?>()
            scope.add(Strings.SCOPE_CURRENT_ENTRY)
            i.putExtra(
                Strings.EXTRA_ACCESS_TOKEN, AccessManager.getAccessToken(
                    context, hostPackage, scope
                )
            )
            i.setPackage(hostPackage)
            i.putExtra(Strings.EXTRA_SENDER, context.packageName)
            i.putExtra(Strings.EXTRA_FIELD_VALUE, fieldValue)
            i.putExtra(Strings.EXTRA_ENTRY_ID, entryId)
            i.putExtra(Strings.EXTRA_FIELD_ID, fieldId)
            i.putExtra(Strings.EXTRA_FIELD_PROTECTED, isProtected)

            context.sendBroadcast(i)
        }
    }

    private inner class ActionSelectedAction(ctx: Context, intent: Intent) :
        PluginEntryActionBase(ctx, intent) {
        val actionData: Bundle?
            /**
             *
             * @return the Bundle associated with the action. This bundle can be set in OpenEntry.add(Entry)FieldAction
             */
            get() = _intent.getBundleExtra(Strings.EXTRA_ACTION_DATA)

        private val fieldId: String?
            /**
             *
             * @return the field id which was selected. null if an entry action (in the options menu) was selected.
             */
            get() = _intent.getStringExtra(Strings.EXTRA_FIELD_ID)

        val isEntryAction: Boolean
            /**
             *
             * @return true if an entry action, i.e. an option from the options menu, was selected. False if an option
             * in a popup menu for a certain field was selected.
             */
            get() = fieldId == null

        val entryFields: HashMap<String, String>
            /**
             *
             * @return a hashmap containing the entry fields in key/value form
             */
            get() = entryFieldsFromIntent

        val protectedFieldsList: Array<String?>?
            /**
             *
             * @return an array with the keys of all protected fields in the entry
             */
            get() = protectedFieldsListFromIntent
    }

    private inner class CloseEntryViewAction(context: Context, intent: Intent) :
        PluginEntryActionBase(context, intent) {
        override val entryId: String?
            get() = _intent.getStringExtra(Strings.EXTRA_ENTRY_ID)
    }

    private open inner class OpenEntryAction(context: Context, intent: Intent) :
        PluginEntryActionBase(context, intent) {
        val entryFields: HashMap<String, String>
            get() = entryFieldsFromIntent

        val protectedFieldsList: Array<String?>?
            /**
             *
             * @return an array with the keys of all protected fields in the entry
             */
            get() = protectedFieldsListFromIntent

        fun addEntryAction(
            actionDisplayText: String?,
            actionIconResourceId: Int,
            actionData: Bundle?
        ) {
            addEntryFieldAction(null, null, actionDisplayText, actionIconResourceId, actionData)
        }

        fun addEntryFieldAction(
            actionId: String?,
            fieldId: String?,
            actionDisplayText: String?,
            actionIconResourceId: Int,
            actionData: Bundle?
        ) {
            val i = Intent(Strings.ACTION_ADD_ENTRY_ACTION)
            val scope = ArrayList<String?>()
            scope.add(Strings.SCOPE_CURRENT_ENTRY)
            i.putExtra(
                Strings.EXTRA_ACCESS_TOKEN, AccessManager.getAccessToken(
                    context, hostPackage!!, scope
                )
            )
            i.setPackage(hostPackage)
            i.putExtra(Strings.EXTRA_SENDER, context.packageName)
            i.putExtra(Strings.EXTRA_ACTION_DATA, actionData)
            i.putExtra(Strings.EXTRA_ACTION_DISPLAY_TEXT, actionDisplayText)
            i.putExtra(Strings.EXTRA_ACTION_ICON_RES_ID, actionIconResourceId)
            i.putExtra(Strings.EXTRA_ENTRY_ID, entryId)
            i.putExtra(Strings.EXTRA_FIELD_ID, fieldId)
            i.putExtra(Strings.EXTRA_ACTION_ID, actionId)

            context.sendBroadcast(i)
        }
    }

    private inner class DatabaseAction(context: Context, intent: Intent) :
        PluginActionBase(context, intent) {
        val fileDisplayName: String?
            get() = _intent.getStringExtra(Strings.EXTRA_DATABASE_FILE_DISPLAYNAME)

        val filePath: String?
            get() = _intent.getStringExtra(Strings.EXTRA_DATABASE_FILEPATH)

        val action: String?
            get() = _intent.action
    }

    //EntryOutputModified is very similar to OpenEntry because it receives the same 
    //data (+ the field id which was modified)
    private inner class EntryOutputModifiedAction(context: Context, intent: Intent) :
        OpenEntryAction(context, intent) {
        val modifiedFieldId: String?
            get() = _intent.getStringExtra(Strings.EXTRA_FIELD_ID)
    }

    override fun onReceive(ctx: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.d(
            "KP2A.pluginsdk",
            "received broadcast in PluginActionBroadcastReceiver with action=$action"
        )
        println(action)

        when (action) {
            Strings.ACTION_OPEN_ENTRY -> openEntry(OpenEntryAction(ctx, intent))
            Strings.ACTION_CLOSE_ENTRY_VIEW -> closeEntryView(CloseEntryViewAction(ctx, intent))
            Strings.ACTION_ENTRY_ACTION_SELECTED ->
                actionSelected(ActionSelectedAction(ctx, intent))

            Strings.ACTION_ENTRY_OUTPUT_MODIFIED ->
                entryOutputModified(EntryOutputModifiedAction(ctx, intent))

            Strings.ACTION_LOCK_DATABASE -> dbAction(DatabaseAction(ctx, intent))
            Strings.ACTION_UNLOCK_DATABASE -> dbAction(DatabaseAction(ctx, intent))
            Strings.ACTION_OPEN_DATABASE -> dbAction(DatabaseAction(ctx, intent))
            Strings.ACTION_CLOSE_DATABASE -> dbAction(DatabaseAction(ctx, intent))

            else -> println(action)
        }
    }

    private fun closeEntryView(closeEntryView: CloseEntryViewAction?) {}

    private fun actionSelected(actionSelected: ActionSelectedAction?) {}

    private fun openEntry(oe: OpenEntryAction?) {}

    private fun entryOutputModified(eom: EntryOutputModifiedAction?) {}

    private fun dbAction(db: DatabaseAction?) {}
}
