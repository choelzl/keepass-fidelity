package net.helcel.fidelity.activity.fragment

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import net.helcel.fidelity.R
import net.helcel.fidelity.activity.adapter.FidelityListAdapter
import net.helcel.fidelity.databinding.FragLauncherBinding
import net.helcel.fidelity.pluginSDK.Kp2aControl
import net.helcel.fidelity.tools.CacheManager
import net.helcel.fidelity.tools.ErrorToaster
import net.helcel.fidelity.tools.KeepassWrapper


class Launcher : Fragment() {

    private lateinit var binding: FragLauncherBinding
    private lateinit var fidelityListAdapter: FidelityListAdapter

    private val resultLauncherQuery = KeepassWrapper.resultLauncherQuery(this) {
        val r = KeepassWrapper.entryExtract(it)
        if (!KeepassWrapper.isProtected(it)) {
            CacheManager.addFidelity(r)
        }
        startViewEntry(r.first, r.second, r.third)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragLauncherBinding.inflate(layoutInflater)
        binding.btnQuery.setOnClickListener { startGetFromKeepass() }
        binding.btnAdd.setOnClickListener {
            if (binding.menuAdd.visibility == View.GONE)
                showMenuAdd()
            else
                hideMenuAdd()
        }

        hideMenuAdd()
        binding.btnScan.setOnClickListener {
            startScanner()
            hideMenuAdd()
        }

        binding.btnManual.setOnClickListener {
            startCreateEntry()
            hideMenuAdd()
        }

        binding.fidelityList.layoutManager =
            LinearLayoutManager(requireContext())
        fidelityListAdapter = FidelityListAdapter(CacheManager.getFidelity()) {
            startViewEntry(it.first, it.second, it.third)
        }
        binding.fidelityList.adapter = fidelityListAdapter
        return binding.root
    }

    private fun hideMenuAdd() {
        binding.btnAdd.setImageResource(R.drawable.cross)
        binding.menuAdd.visibility = View.GONE

    }

    private fun showMenuAdd() {
        binding.btnAdd.setImageResource(R.drawable.minus)
        binding.menuAdd.visibility = View.VISIBLE
    }


    private fun startGetFromKeepass() {
        try {
            this.resultLauncherQuery.launch(Kp2aControl.queryEntryIntentForOwnPackage)
        } catch (e: ActivityNotFoundException) {
            ErrorToaster.noKP2AFound(requireActivity())
        }
    }

    private fun startFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .addToBackStack("Launcher")
            .replace(R.id.container, fragment).commit()
    }

    private fun startScanner() {
        startFragment(Scanner())
    }

    private fun startCreateEntry() {
        startFragment(CreateEntry())
    }


    private fun startViewEntry(title: String?, code: String?, fmt: String?) {
        val viewEntryFragment = ViewEntry()
        viewEntryFragment.arguments = KeepassWrapper.bundleCreate(title, code, fmt)
        startFragment(viewEntryFragment)
    }
}