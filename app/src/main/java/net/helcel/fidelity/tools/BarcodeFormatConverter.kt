package net.helcel.fidelity.tools

import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat

object BarcodeFormatConverter {

    fun stringToFormat(f: String): BarcodeFormat {
        return when (f) {
            "CODE_128" -> BarcodeFormat.CODE_128
            "CODE_39" -> BarcodeFormat.CODE_39
            "CODE_93" -> BarcodeFormat.CODE_93
            "EAN_8" -> BarcodeFormat.EAN_8
            "EAN_13" -> BarcodeFormat.EAN_13
            "CODE_QR" -> BarcodeFormat.QR_CODE
            "UPC_A" -> BarcodeFormat.UPC_A
            "UPC_E" -> BarcodeFormat.UPC_E
            "PDF_417" -> BarcodeFormat.PDF_417
            else -> throw Exception("Unsupported Format: $f")
        }
    }


    fun formatToString(f: Int): String {
        return when (f) {
            Barcode.FORMAT_CODE_128 -> "CODE_128"
            Barcode.FORMAT_CODE_39 -> "CODE_39"
            Barcode.FORMAT_CODE_93 -> "CODE_93"
            Barcode.FORMAT_EAN_8 -> "EAN_8"
            Barcode.FORMAT_EAN_13 -> "EAN_13"
            Barcode.FORMAT_QR_CODE -> "CODE_QR"
            Barcode.FORMAT_UPC_A -> "UPC_A"
            Barcode.FORMAT_UPC_E -> "UPC_E"
            Barcode.FORMAT_PDF417 -> "PDF_417"
            else -> throw Exception("Unsupported Format: $f")
        }
    }
}