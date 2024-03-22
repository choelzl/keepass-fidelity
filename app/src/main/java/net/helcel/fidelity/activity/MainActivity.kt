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
import net.helcel.fidelity.databinding.ActMainBinding
import net.helcel.fidelity.tools.CacheManager

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

        if (savedInstanceState == null)
            loadLauncher()
    }

    private fun loadLauncher() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, Launcher())
            .commit()
    }
}

