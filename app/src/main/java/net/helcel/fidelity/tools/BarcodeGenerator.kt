package net.helcel.fidelity.tools

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import net.helcel.fidelity.tools.BarcodeFormatConverter.stringToFormat

object BarcodeGenerator {

    private fun getPixelColor(bitMatrix: BitMatrix, x: Int, y: Int): Int {
        if (x >= bitMatrix.width || y >= bitMatrix.height)
            return android.graphics.Color.WHITE

        return if (bitMatrix[x, y])
            android.graphics.Color.BLACK
        else
            android.graphics.Color.WHITE
    }

    fun generateBarcode(content: String, f: String, width: Int): Bitmap? {
        if (content.isEmpty() || f.isEmpty()) {
            return null
        }
        try {
            val format = stringToFormat(f)
            val writer = MultiFormatWriter()
            val height = (formatToRatio(format) * width).toInt()
            val bitMatrix: BitMatrix = writer.encode(content, format, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x,
                        y,
                        getPixelColor(bitMatrix, x, y)
                    )

                }
            }
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }

    private fun formatToRatio(format: BarcodeFormat): Double {
        return when (format) {
            BarcodeFormat.QR_CODE -> 1.0
            BarcodeFormat.PDF_417 -> 0.4
            else -> 0.5
        }
    }
}