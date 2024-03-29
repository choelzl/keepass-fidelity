package net.helcel.fidelity.activity.fragment

import android.Manifest
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import net.helcel.fidelity.R
import net.helcel.fidelity.tools.BarcodeScanner
import net.helcel.fidelity.tools.ErrorToaster
import net.helcel.fidelity.tools.KeepassWrapper
import java.io.FileNotFoundException

class FileScanner : Fragment() {

    private var code: String = ""
    private var fmt: String = ""


    private val resultPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            resultLauncherOpenMediaPick.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

    private val resultLauncherOpenMediaBase =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            loadUri(it)
        }

    private val resultLauncherOpenMediaPick =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            loadUri(it)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        println(Build.VERSION.SDK_INT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            resultPermission.launch(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        } else {
            // resultLauncherOpenMediaBase.launch("image/*")
            resultLauncherOpenMediaPick.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        return View(context)
    }

    private fun startCreateEntry() {
        val createEntryFragment = CreateEntry()
        createEntryFragment.arguments =
            KeepassWrapper.bundleCreate(null, this.code, this.fmt)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, createEntryFragment)
            .commit()
    }

    private fun scannerResult(code: String?, format: String?) {
        if (!code.isNullOrEmpty() && !format.isNullOrEmpty()) {
            this.code = code
            this.fmt = format
        }
        val isDone = this.code.isNotEmpty() && this.fmt.isNotEmpty()
        requireActivity().runOnUiThread {
            if (isDone) {
                startCreateEntry()
            } else {
                parentFragmentManager.popBackStack()
                ErrorToaster.nothingFound(context)
            }
        }
    }

    private fun loadUri(it: Uri?) {
        try {
            run {
                require(it != null)

                val file = requireContext().contentResolver.openInputStream(it)
                val image = BitmapFactory.decodeStream(file)
                BarcodeScanner.bitmapUseCase(image) { code, format ->
                    scannerResult(code, format)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            println(e.message)
            println(it)
            ErrorToaster.noPermission(context)
            parentFragmentManager.popBackStack()
        } catch (e: IllegalArgumentException) {
            ErrorToaster.nothingFound(context)
            parentFragmentManager.popBackStack()
        } catch (e: SecurityException) {
            ErrorToaster.noPermission(context)
            parentFragmentManager.popBackStack()
        }
    }

}