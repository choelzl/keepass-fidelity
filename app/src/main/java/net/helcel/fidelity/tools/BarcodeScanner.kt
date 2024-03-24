package net.helcel.fidelity.tools

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.ReaderException
import com.google.zxing.common.HybridBinarizer
import net.helcel.fidelity.tools.BarcodeFormatConverter.formatToString
import java.util.concurrent.Executors


object BarcodeScanner {

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(
        imageProxy: ImageProxy,
        cb: (String?, String?) -> Unit
    ) {
        val bitmap = imageProxy.toBitmap() // Convert ImageProxy to Bitmap
        val binaryBitmap = createBinaryBitmap(bitmap)
        val reader = MultiFormatReader()
        try {
            val result = reader.decode(binaryBitmap)
            cb(result.text, formatToString(result.barcodeFormat))
        } catch (e: NotFoundException) {
            cb(null, null)
        } catch (e: ReaderException) {
            cb(null, null)
        } finally {
            imageProxy.close()
        }
    }

    private fun createBinaryBitmap(bitmap: Bitmap): BinaryBitmap {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source =
            RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
        return BinaryBitmap(HybridBinarizer(source))
    }

    fun getAnalysisUseCase(cb: (String?, String?) -> Unit): ImageAnalysis {
        val analysisUseCase = ImageAnalysis.Builder().build()
        analysisUseCase.setAnalyzer(
            Executors.newSingleThreadExecutor()
        ) { imageProxy ->
            processImageProxy(imageProxy, cb)
        }
        return analysisUseCase
    }
}