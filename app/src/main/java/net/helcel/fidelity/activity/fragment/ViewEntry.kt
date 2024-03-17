package net.helcel.fidelity.activity.fragment

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
import android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
import androidx.fragment.app.Fragment
import com.google.zxing.FormatException
import net.helcel.fidelity.databinding.FragViewEntryBinding
import net.helcel.fidelity.tools.BarcodeGenerator.generateBarcode
import net.helcel.fidelity.tools.ErrorToaster
import net.helcel.fidelity.tools.KeepassWrapper

@SuppressLint("SourceLockedOrientationActivity")
class ViewEntry : Fragment() {

    private lateinit var binding: FragViewEntryBinding
    private var title: String? = null
    private var code: String? = null
    private var fmt: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragViewEntryBinding.inflate(layoutInflater)
        val res = KeepassWrapper.bundleExtract(arguments)
        title = res.first
        code = res.second
        fmt = res.third

        updatePreview()
        updateLayout()

        binding.imageViewPreview.setOnClickListener {
            requireActivity().requestedOrientation =
                if (isLandscape()) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        return binding.root
    }

    private fun updatePreview() {
        binding.title.text = title
        try {
            val barcodeBitmap = generateBarcode(
                code, fmt, 1024
            )
            binding.imageViewPreview.setImageBitmap(barcodeBitmap)
        } catch (e: FormatException) {
            ErrorToaster.invalidFormat(requireActivity())
            binding.imageViewPreview.setImageBitmap(null)
        } catch (e: IllegalArgumentException) {
            binding.imageViewPreview.setImageBitmap(null)
            ErrorToaster.invalidFormat(requireActivity())
        } catch (e: Exception) {
            binding.imageViewPreview.setImageBitmap(null)
            e.printStackTrace()
        }
    }

    private fun updateLayout() {
        if (isLandscape()) {
            binding.title.visibility = View.GONE
            setScreenBrightness(BRIGHTNESS_OVERRIDE_FULL)
        } else {
            binding.title.visibility = View.VISIBLE
            setScreenBrightness(BRIGHTNESS_OVERRIDE_NONE)
        }
    }

    private fun isLandscape(): Boolean {
        return (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
    }

    private fun setScreenBrightness(brightness: Float?) {
        requireActivity().window?.attributes?.screenBrightness = brightness
    }
}