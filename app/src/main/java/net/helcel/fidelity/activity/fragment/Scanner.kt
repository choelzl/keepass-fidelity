package net.helcel.fidelity.activity.fragment

import android.Manifest
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import net.helcel.fidelity.R
import net.helcel.fidelity.databinding.FragScannerBinding
import net.helcel.fidelity.tools.BarcodeScanner.analysisUseCase
import net.helcel.fidelity.tools.ErrorToaster
import net.helcel.fidelity.tools.KeepassWrapper

class Scanner : Fragment() {

    private lateinit var binding: FragScannerBinding

    private var code: String = ""
    private var fmt: String = ""


    private val resultPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                bindCameraUseCases()
            } else {
                parentFragmentManager.popBackStack()
                ErrorToaster.noPermission(context)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragScannerBinding.inflate(layoutInflater)
        binding.btnScanDone.setOnClickListener {
            startCreateEntry()
        }
        binding.btnScanDone.isEnabled = false
        resultPermissionRequest.launch(Manifest.permission.CAMERA)
        return binding.root
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
        activity?.runOnUiThread {
            binding.btnScanDone.isEnabled = isDone
            binding.ScanActive.isEnabled = !isDone
        }
    }

    private fun bindCameraUseCases() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val previewUseCase = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraView.surfaceProvider)
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val analysisUseCase = analysisUseCase { code, format ->
                scannerResult(code, format)
            }
            try {
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    previewUseCase,
                    analysisUseCase
                )
            } catch (illegalStateException: IllegalStateException) {
                Log.e(ContentValues.TAG, illegalStateException.message.orEmpty())
            } catch (illegalArgumentException: IllegalArgumentException) {
                Log.e(ContentValues.TAG, illegalArgumentException.message.orEmpty())
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }
}