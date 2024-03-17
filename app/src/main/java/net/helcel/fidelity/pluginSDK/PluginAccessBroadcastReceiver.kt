package net.helcel.fidelity.pluginSDK

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Broadcast flow between Host and Plugin
 * ======================================
 *
 * The host is responsible for deciding when to initiate the session. It
 * should initiate the session as soon as plugins are required or when a plugin
 * has been updated through the OS.
 * It will then send a broadcast to request the currently required scope.
 * The plugin then sends a broadcast to the app which scope is required. If an
 * access token is already available, it's sent along with the requset.
 *
 * If a previous permission has been revoked (or the app settings cleared or the
 * permissions have been extended or the token is invalid for any other reason)
 * the host will answer with a Revoked-Permission broadcast (i.e. the plugin is
 * unconnected.)
 *
 * Unconnected plugins must be permitted by the user (requiring user action).
 * When the user grants access, the plugin will receive an access token for
 * the host. This access token is valid for the requested scope. If the scope
 * changes (e.g after an update of the plugin), the access token becomes invalid.
 *
 */
abstract class PluginAccessBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        val action = intent.action
        Log.d(_tag, "received broadcast with action=$action")
        if (action == null) return
        when (action) {
            Strings.ACTION_TRIGGER_REQUEST_ACCESS -> requestAccess(ctx, intent)
            Strings.ACTION_RECEIVE_ACCESS -> receiveAccess(ctx, intent)
            Strings.ACTION_REVOKE_ACCESS -> revokeAccess(ctx, intent)
            else -> {}
        }
    }


    private fun revokeAccess(ctx: Context, intent: Intent) {
        val senderPackage = intent.getStringExtra(Strings.EXTRA_SENDER)
        val accessToken = intent.getStringExtra(Strings.EXTRA_ACCESS_TOKEN)
        AccessManager.removeAccessToken(ctx, senderPackage!!, accessToken!!)
    }


    private fun receiveAccess(ctx: Context, intent: Intent) {
        val senderPackage = intent.getStringExtra(Strings.EXTRA_SENDER)
        val accessToken = intent.getStringExtra(Strings.EXTRA_ACCESS_TOKEN)
        AccessManager.storeAccessToken(ctx, senderPackage!!, accessToken!!, scopes)
    }

    private fun requestAccess(ctx: Context, intent: Intent) {
        val senderPackage = intent.getStringExtra(Strings.EXTRA_SENDER)
        val requestToken = intent.getStringExtra(Strings.EXTRA_REQUEST_TOKEN)
        val rpi = Intent(Strings.ACTION_REQUEST_ACCESS)
        rpi.setPackage(senderPackage)
        rpi.putExtra(Strings.EXTRA_SENDER, ctx.packageName)
        rpi.putExtra(Strings.EXTRA_REQUEST_TOKEN, requestToken)

        val token: String? = AccessManager.tryGetAccessToken(ctx, senderPackage!!, scopes)
        rpi.putExtra(Strings.EXTRA_ACCESS_TOKEN, token)

        rpi.putStringArrayListExtra(Strings.EXTRA_SCOPES, scopes)
        Log.d(_tag, "requesting access for " + scopes.size + " tokens.")
        ctx.sendBroadcast(rpi)
    }

    /**
     *
     * @return the list of required scopes for this plugin.
     */
    abstract val scopes: ArrayList<String?>

    companion object {
        private const val _tag = "Kp2aPluginSDK"
    }
}
