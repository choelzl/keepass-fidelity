package net.helcel.fidelity.pluginSDK

object KeepassDefs {
    /// <summary>
    /// Default identifier string for the title field. Should not contain
    /// spaces, tabs or other whitespace.
    /// </summary>
    var TitleField: String = "Title"

    /// <summary>
    /// Default identifier string for the user name field. Should not contain
    /// spaces, tabs or other whitespace.
    /// </summary>
    private var UserNameField: String = "UserName"

    /// <summary>
    /// Default identifier string for the password field. Should not contain
    /// spaces, tabs or other whitespace.
    /// </summary>
    private var PasswordField: String = "Password"

    /// <summary>
    /// Default identifier string for the URL field. Should not contain
    /// spaces, tabs or other whitespace.
    /// </summary>
    var UrlField: String = "URL"

    /// <summary>
    /// Default identifier string for the notes field. Should not contain
    /// spaces, tabs or other whitespace.
    /// </summary>
    private var NotesField: String = "Notes"


    fun IsStandardField(strFieldName: String?): Boolean {
        if (strFieldName == null) return false
        if (strFieldName == TitleField) return true
        if (strFieldName == UserNameField) return true
        if (strFieldName == PasswordField) return true
        if (strFieldName == UrlField) return true
        if (strFieldName == NotesField) return true

        return false
    }
}
