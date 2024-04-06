package net.helcel.fidelity.activity.fragment

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.zxing.FormatException
import net.helcel.fidelity.R
import net.helcel.fidelity.databinding.FragCreateEntryBinding
import net.helcel.fidelity.pluginSDK.Kp2aControl
import net.helcel.fidelity.tools.BarcodeGenerator.generateBarcode
import net.helcel.fidelity.tools.CacheManager
import net.helcel.fidelity.tools.ErrorToaster
import net.helcel.fidelity.tools.KeepassWrapper

private const val DEBOUNCE_DELAY = 500L

class CreateEntry : Fragment() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var binding: FragCreateEntryBinding

    private val resultLauncherAdd = KeepassWrapper.resultLauncher(this) {
        val r = KeepassWrapper.entryExtract(it)
        if (!KeepassWrapper.isProtected(it)) {
            CacheManager.addFidelity(r)
        }
        startViewEntry(r.first, r.second, r.third)
    }

    private var isValidBarcode: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragCreateEntryBinding.inflate(layoutInflater)

        val formats = resources.getStringArray(R.array.format_array)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_item_dropdown, formats)
        binding.editTextFormat.setAdapter(arrayAdapter)

        val res = KeepassWrapper.bundleExtract(arguments)
        binding.editTextCode.setText(res.second)
        binding.editTextFormat.setText(res.third, false)

        binding.editTextCode.addTextChangedListener { changeListener() }
        binding.editTextFormat.addTextChangedListener { changeListener() }
        binding.editTextFormat.addTextChangedListener { binding.editTextFormat.error = null }
        binding.btnSave.setOnClickListener { submit() }

        binding.editTextTitle.onDone { submit() }
        binding.editTextCode.onDone { submit() }


        updatePreview()
        return binding.root
    }

    private fun updatePreview() {
        try {
            val barcodeBitmap = generateBarcode(
                binding.editTextCode.text.toString(),
                binding.editTextFormat.text.toString(),
                600
            )
            binding.imageViewPreview.setImageBitmap(barcodeBitmap)
            isValidBarcode = true
        } catch (e: FormatException) {
            binding.imageViewPreview.setImageBitmap(null)
            binding.editTextCode.error = "Invalid format"
        } catch (e: IllegalArgumentException) {
            binding.imageViewPreview.setImageBitmap(null)
            binding.editTextCode.error = e.message
        } catch (e: Exception) {
            binding.imageViewPreview.setImageBitmap(null)
            e.printStackTrace()
        }
    }

    private fun isValidForm(): Boolean {
        var valid = true
        if (binding.editTextFormat.text.isNullOrEmpty()) {
            valid = false
            binding.editTextFormat.error = "Format cannot be empty"
        }
        if (binding.editTextCode.text.isNullOrEmpty()) {
            valid = false
            binding.editTextCode.error = "Code cannot be empty"
        }
        if (binding.editTextTitle.text.isNullOrEmpty()) {
            valid = false
            binding.editTextTitle.error = "Title cannot be empty"
        }
        return valid
    }


    private fun startViewEntry(title: String?, code: String?, fmt: String?) {
        val viewEntryFragment = ViewEntry()
        viewEntryFragment.arguments = KeepassWrapper.bundleCreate(title, code, fmt)

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, viewEntryFragment).commit()
    }


    private fun changeListener() {
        isValidBarcode = false
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            updatePreview()
        }, DEBOUNCE_DELAY)
    }


    private fun TextInputEditText.onDone(callback: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                callback.invoke()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun submit() {
        if (!isValidForm() || !isValidBarcode) {
            ErrorToaster.formIncomplete(context)
        } else {
            val kpEntry = KeepassWrapper.entryCreate(
                this,
                binding.editTextTitle.text.toString(),
                binding.editTextCode.text.toString(),
                binding.editTextFormat.text.toString(),
                binding.checkboxProtected.isChecked,
            )
            try {
                resultLauncherAdd.launch(
                    Kp2aControl.getAddEntryIntent(
                        kpEntry.first,
                        kpEntry.second
                    )
                )
                if (!binding.checkboxProtected.isChecked) {
                    val r = KeepassWrapper.entryExtract(kpEntry.first)
                    CacheManager.addFidelity(r)
                }
                activity?.supportFragmentManager?.popBackStack()
            } catch (e: ActivityNotFoundException) {
                ErrorToaster.noKP2AFound(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}