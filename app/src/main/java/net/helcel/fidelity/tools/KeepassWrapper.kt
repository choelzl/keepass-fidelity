package net.helcel.fidelity.tools

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import net.helcel.fidelity.pluginSDK.KeepassDef
import net.helcel.fidelity.pluginSDK.Kp2aControl

object KeepassWrapper {

    private const val CODE_FIELD: String = "FidelityCode"
    private const val FORMAT_FIELD: String = "FidelityFormat"
    private const val PROTECT_CODE_FIELD: String = "FidelityProtectedCode"

    fun entryCreate(
        fragment: Fragment,
        title: String,
        code: String,
        format: String,
        protectCode: Boolean,
    ): Pair<HashMap<String, String>, ArrayList<String>> {

        val fields = HashMap<String, String>()
        val protected = ArrayList<String>()
        fields[KeepassDef.TitleField] = title
        fields[KeepassDef.UrlField] =
            "androidapp://" + fragment.requireActivity().packageName
        fields[CODE_FIELD] = code
        fields[FORMAT_FIELD] = format
        fields[PROTECT_CODE_FIELD] = protectCode.toString()
        protected.add(CODE_FIELD)

        return Pair(fields, protected)
    }


    fun resultLauncher(
        fragment: Fragment,
        callback: (HashMap<String, String>) -> Unit
    ): ActivityResultLauncher<Intent> {
        return fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val credentials = Kp2aControl.getEntryFieldsFromIntent(result.data)
                callback(credentials)
            }
        }
    }

    fun entryExtract(map: HashMap<String, String>): Triple<String?, String?, String?> {
        return Triple(
            map[KeepassDef.TitleField],
            map[CODE_FIELD],
            map[FORMAT_FIELD]
        )
    }

    fun bundleCreate(title: String?, code: String?, fmt: String?): Bundle {
        val data = Bundle()
        data.putString("title", title)
        data.putString("code", code)
        data.putString("fmt", fmt)
        return data
    }

    fun bundleCreate(triple: Triple<String?, String?, String?>): Bundle {
        return bundleCreate(triple.first, triple.second, triple.third)
    }

    fun bundleExtract(data: Bundle?): Triple<String?, String?, String?> {
        return Triple(
            data?.getString("title"),
            data?.getString("code"),
            data?.getString("fmt")
        )
    }

    fun isProtected(map: HashMap<String, String>): Boolean {
        return map[PROTECT_CODE_FIELD].toBoolean()
    }


}