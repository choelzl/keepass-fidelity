package net.helcel.fidelity.activity.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.zxing.FormatException
import net.helcel.fidelity.databinding.FragViewEntryBinding
import net.helcel.fidelity.tools.BarcodeGenerator.generateBarcode
import net.helcel.fidelity.tools.ErrorToaster
import net.helcel.fidelity.tools.KeepassWrapper


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

        adjustLayout()
        updatePreview()
        return binding.root
    }

    private fun updatePreview() {
        binding.title.text = title
        try {
            val barcodeBitmap = generateBarcode(
                code!!, fmt!!, 1024
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
            println(e.javaClass)
            println(e.message)
            e.printStackTrace()
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustLayout()
    }

    private fun adjustLayout() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.title.visibility = View.GONE
        } else {
            binding.title.visibility = View.VISIBLE
        }
    }
}