package net.helcel.fidelity.pluginSDK

object Strings {
    /**
     * Plugin is notified about actions like open/close/update a database.
     */
    const val SCOPE_DATABASE_ACTIONS = "keepass2android.SCOPE_DATABASE_ACTIONS"

    /**
     * Plugin is notified when an entry is opened.
     */
    const val SCOPE_CURRENT_ENTRY = "keepass2android.SCOPE_CURRENT_ENTRY"

    /**
     * Plugin may query credentials for its own package
     */
    const val SCOPE_QUERY_CREDENTIALS_FOR_OWN_PACKAGE =
        "keepass2android.SCOPE_QUERY_CREDENTIALS_FOR_OWN_PACKAGE"

    /**
     * Extra key to transfer a (json serialized) list of scopes
     */
    const val EXTRA_SCOPES = "keepass2android.EXTRA_SCOPES"


    const val EXTRA_PLUGIN_PACKAGE = "keepass2android.EXTRA_PLUGIN_PACKAGE"

    /**
     * Extra key for sending the package name of the sender of a broadcast.
     * Should be set in every broadcast.
     */
    const val EXTRA_SENDER = "keepass2android.EXTRA_SENDER"

    /**
     * Extra key for sending a request token. The request token is passed from
     * KP2A to the plugin. It's used in the authorization process.
     */
    const val EXTRA_REQUEST_TOKEN = "keepass2android.EXTRA_REQUEST_TOKEN"

    /**
     * Action to start KP2A with an AppTask
     */
    const val ACTION_START_WITH_TASK = "keepass2android.ACTION_START_WITH_TASK"

    /**
     * Action sent from KP2A to the plugin to indicate that the plugin should request
     * access (sending it's scopes)
     */
    const val ACTION_TRIGGER_REQUEST_ACCESS = "keepass2android.ACTION_TRIGGER_REQUEST_ACCESS"

    /**
     * Action sent from the plugin to KP2A including the scopes.
     */
    const val ACTION_REQUEST_ACCESS = "keepass2android.ACTION_REQUEST_ACCESS"

    /**
     * Action sent from the KP2A to the plugin when the user grants access.
     * Will contain an access token.
     */
    const val ACTION_RECEIVE_ACCESS = "keepass2android.ACTION_RECEIVE_ACCESS"

    /**
     * Action sent from KP2A to the plugin to indicate that access is not or no longer valid.
     */
    const val ACTION_REVOKE_ACCESS = "keepass2android.ACTION_REVOKE_ACCESS"


    /**
     * Action for startActivity(). Opens an activity in the Plugin Host to edit the plugin settings (i.e. enable it)
     */
    const val ACTION_EDIT_PLUGIN_SETTINGS = "keepass2android.ACTION_EDIT_PLUGIN_SETTINGS"

    /**
     * Action sent from KP2A to the plugin to indicate that an entry was opened.
     * The Intent contains the full entry data.
     */
    const val ACTION_OPEN_ENTRY = "keepass2android.ACTION_OPEN_ENTRY"

    /**
     * Action sent from KP2A to the plugin to indicate that an entry output field was modified/added.
     * The Intent contains the full new entry data.
     */
    const val ACTION_ENTRY_OUTPUT_MODIFIED = "keepass2android.ACTION_ENTRY_OUTPUT_MODIFIED"

    /**
     * Action sent from KP2A to the plugin to indicate that an entry activity was closed.
     */
    const val ACTION_CLOSE_ENTRY_VIEW = "keepass2android.ACTION_CLOSE_ENTRY_VIEW"

    /**
     * Extra key for a string containing the GUID of the entry.
     */
    const val EXTRA_ENTRY_ID = "keepass2android.EXTRA_ENTRY_DATA"


    /**
     * Json serialized list of fields, transformed using the database context (i.e. placeholders are replaced already)
     */
    const val EXTRA_ENTRY_OUTPUT_DATA = "keepass2android.EXTRA_ENTRY_OUTPUT_DATA"

    /**
     * Json serialized lisf of field keys, specifying which field of the EXTRA_ENTRY_OUTPUT_DATA is protected.
     */
    const val EXTRA_PROTECTED_FIELDS_LIST = "keepass2android.EXTRA_PROTECTED_FIELDS_LIST"


    /**
     * Extra key for passing the access token (both ways)
     */
    const val EXTRA_ACCESS_TOKEN = "keepass2android.EXTRA_ACCESS_TOKEN"

    /**
     * Action for an intent from the plugin to KP2A to add menu options regarding the currently open entry.
     * Requires SCOPE_CURRENT_ENTRY.
     */
    const val ACTION_ADD_ENTRY_ACTION = "keepass2android.ACTION_ADD_ENTRY_ACTION"

    const val EXTRA_ACTION_DISPLAY_TEXT = "keepass2android.EXTRA_ACTION_DISPLAY_TEXT"
    const val EXTRA_ACTION_ICON_RES_ID = "keepass2android.EXTRA_ACTION_ICON_RES_ID"

    const val EXTRA_FIELD_ID = "keepass2android.EXTRA_FIELD_ID"

    /**
     * Used to pass an id for the action. Each actionId may occur only once per field, otherwise the previous
     * action with same id is replaced by the new action.
     */
    const val EXTRA_ACTION_ID = "keepass2android.EXTRA_ACTION_ID"

    /** Extra for ACTION_ADD_ENTRY_ACTION and ACTION_ENTRY_ACTION_SELECTED to pass data specifying the action parameters.*/
    const val EXTRA_ACTION_DATA = "keepass2android.EXTRA_ACTION_DATA"

    /**
     * Action for an intent from KP2A to the plugin when an action added with ACTION_ADD_ENTRY_ACTION was selected by the user.
     *
     */
    const val ACTION_ENTRY_ACTION_SELECTED = "keepass2android.ACTION_ENTRY_ACTION_SELECTED"

    /**
     * Extra key for the string which is used to query the credentials. This should be either a URL for
     * a web login (google.com or a full URI) or something in the form "androidapp://com.my.package"
     */
    const val EXTRA_QUERY_STRING = "keepass2android.EXTRA_QUERY_STRING"

    /**
     * Action when plugin wants to query credentials for its own package
     */
    const val ACTION_QUERY_CREDENTIALS_FOR_OWN_PACKAGE =
        "keepass2android.ACTION_QUERY_CREDENTIALS_FOR_OWN_PACKAGE"

    /**
     * Action for an intent from the plugin to KP2A to set (i.e. add or update) a field in the entry.
     * May be used to update existing or add new fields at any time while the entry is opened.
     */
    const val ACTION_SET_ENTRY_FIELD = "keepass2android.ACTION_SET_ENTRY_FIELD"

    /** Actions for an intent from KP2A to the plugin to inform that a database was opened, closed, quicklocked or quickunlocked.*/
    const val ACTION_OPEN_DATABASE = "keepass2android.ACTION_OPEN_DATABASE"
    const val ACTION_CLOSE_DATABASE = "keepass2android.ACTION_CLOSE_DATABASE"
    const val ACTION_LOCK_DATABASE = "keepass2android.ACTION_LOCK_DATABASE"
    const val ACTION_UNLOCK_DATABASE = "keepass2android.ACTION_UNLOCK_DATABASE"

    /** Extra for ACTION_OPEN_DATABASE and ACTION_CLOSE_DATABASE containing a filepath which is used
     * by KP2A internally to identify the file. Use only where necessary, might contain credentials
     * for accessing the file (on remote storage).*/
    const val EXTRA_DATABASE_FILEPATH = "keepass2android.EXTRA_DATABASE_FILEPATH"

    /** Extra for ACTION_OPEN_DATABASE and ACTION_CLOSE_DATABASE containing a filepath which can be
     * displayed to the user.*/
    const val EXTRA_DATABASE_FILE_DISPLAYNAME = "keepass2android.EXTRA_DATABASE_FILE_DISPLAYNAME"


    const val EXTRA_FIELD_VALUE = "keepass2android.EXTRA_FIELD_VALUE"
    const val EXTRA_FIELD_PROTECTED = "keepass2android.EXTRA_FIELD_PROTECTED"


}
