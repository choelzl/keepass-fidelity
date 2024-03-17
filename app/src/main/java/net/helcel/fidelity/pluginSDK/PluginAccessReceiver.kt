package net.helcel.fidelity.pluginSDK

import kotlin.collections.ArrayList


class PluginAccessReceiver : PluginAccessBroadcastReceiver() {

    override val scopes: ArrayList<String?> = ArrayList()

    init {
        this.scopes.add(Strings.SCOPE_DATABASE_ACTIONS)
        this.scopes.add(Strings.SCOPE_QUERY_CREDENTIALS_FOR_OWN_PACKAGE)
    }

}

