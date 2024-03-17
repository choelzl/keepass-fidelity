package net.helcel.fidelity.pluginSDK

class PluginAccessException : Exception {
    constructor(what: String?) : super(what)

    constructor(hostPackage: String?, scopes: ArrayList<String?>)

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 1L
    }
}
