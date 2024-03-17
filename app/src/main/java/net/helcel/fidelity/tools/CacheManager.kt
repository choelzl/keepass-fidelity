package net.helcel.fidelity.tools

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object CacheManager {

    const val PREF_NAME = "FIDELITY"
    private const val ENTRY_KEY = "FIDELITY"
    private var data: ArrayList<Triple<String?, String?, String?>> = ArrayList()
    private var pref: SharedPreferences? = null

    fun addFidelity(item: Triple<String?, String?, String?>) {
        val exists = data.find { it.first == item.first }
        if (exists != null)
            data.remove(exists)

        data.add(0, item)
        saveFidelity()
    }

    fun rmFidelity(idx: Int) {
        data.removeAt(idx)
        saveFidelity()
    }

    private fun saveFidelity() {
        val editor = pref?.edit()
        val gson = Gson()
        val json = gson.toJson(data)
        editor?.putString(ENTRY_KEY, json)
        editor?.apply()
    }

    fun loadFidelity(pref: SharedPreferences) {
        this.pref = pref
        val gson = Gson()
        val json = pref.getString(ENTRY_KEY, null)
        val type = object : TypeToken<List<Triple<String, String, Int>>>() {}.type
        data = gson.fromJson(json, type) ?: ArrayList()

    }

    fun getFidelity(): ArrayList<Triple<String?, String?, String?>> {
        return data
    }

}