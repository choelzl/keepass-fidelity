package net.helcel.fidelity.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import net.helcel.fidelity.R
import net.helcel.fidelity.activity.fragment.Launcher
import net.helcel.fidelity.activity.fragment.ViewEntry
import net.helcel.fidelity.databinding.ActMainBinding
import net.helcel.fidelity.pluginSDK.Kp2aControl.getEntryFieldsFromIntent
import net.helcel.fidelity.tools.CacheManager
import net.helcel.fidelity.tools.KeepassWrapper.bundleCreate
import net.helcel.fidelity.tools.KeepassWrapper.entryExtract

@SuppressLint("SourceLockedOrientationActivity")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActMainBinding
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            this.getSharedPreferences(CacheManager.PREF_NAME, Context.MODE_PRIVATE)
        CacheManager.loadFidelity(sharedPreferences)

        binding = ActMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStackImmediate()
                loadLauncher()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                finish()
            }
        }

        if (intent.extras != null)
            loadViewEntry()
        else if (savedInstanceState == null)
            loadLauncher()
    }

    private fun loadLauncher() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, Launcher())
            .commit()
    }

    private fun loadViewEntry() {
        val viewEntry = ViewEntry()
        val data = getEntryFieldsFromIntent(intent)
        viewEntry.arguments = bundleCreate(entryExtract(data))
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, viewEntry)
            .commit()
    }
}

