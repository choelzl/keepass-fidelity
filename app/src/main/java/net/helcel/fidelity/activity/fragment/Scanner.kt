package net.helcel.fidelity.activity.fragment

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import net.helcel.fidelity.R
import net.helcel.fidelity.databinding.FragScannerBinding
import net.helcel.fidelity.tools.BarcodeScanner.getAnalysisUseCase
import net.helcel.fidelity.tools.KeepassWrapper

private const val CAMERA_PERMISSION_REQUEST_CODE = 1

class Scanner : Fragment() {

    private lateinit var binding: FragScannerBinding

    private var code: String = ""
    private var fmt: String = ""
    private var valid: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragScannerBinding.inflate(layoutInflater)
        binding.bottomText.setOnClickListener {
            startCreateEntry()
        }
        when (hasCameraPermission()) {
            true -> bindCameraUseCases()
            else -> requestPermission()
        }
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

    private fun hasCameraPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
        ActivityCompat.OnRequestPermissionsResultCallback { c, p, i ->
            require(c == CAMERA_PERMISSION_REQUEST_CODE)
            require(p.contains(Manifest.permission.CAMERA))
            val el = i[p.indexOf(Manifest.permission.CAMERA)]
            if (el != PackageManager.PERMISSION_GRANTED) {
                startCreateEntry()
            }

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
            val analysisUseCase = getAnalysisUseCase { code, format ->
                if (code != null && format != null) {
                    this.code = code
                    this.fmt = format
                    this.valid = true
                } else {
                    this.valid = false
                }
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