package com.application.frontend.data.local

import android.content.Context
import com.application.frontend.ui.screen.ScanHistoryDto
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

@Singleton
class ScanHistoryStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun readAll(): List<ScanHistoryDto> = synchronized(lock) {
        val raw = preferences.getString(KEY_HISTORY, null) ?: return emptyList()
        try {
            val jsonArray = JSONArray(raw)
            buildList(jsonArray.length()) {
                for (index in 0 until jsonArray.length()) {
                    val entry = jsonArray.optJSONObject(index) ?: continue
                    val id = entry.optLong("id", System.currentTimeMillis())
                    val category = entry.optString("category")
                    val scannedAt = entry.optString("scannedAt")
                    val leafPoints = entry.optInt("leafPoints", 0)
                    val confirmed = entry.optBoolean("confirmed", true)
                    if (category.isBlank() || scannedAt.isBlank()) continue
                    add(
                        ScanHistoryDto(
                            id = id,
                            category = category,
                            scannedAt = scannedAt,
                            leafPoints = leafPoints,
                            confirmed = confirmed
                        )
                    )
                }
            }
        } catch (_: JSONException) {
            emptyList()
        }
    }

    fun replaceAll(history: List<ScanHistoryDto>) = synchronized(lock) {
        write(history)
    }

    fun upsert(entry: ScanHistoryDto) = synchronized(lock) {
        val current = readAll().filterNot { it.id == entry.id }
        write(listOf(entry) + current)
    }

    private fun write(history: List<ScanHistoryDto>) {
        val array = JSONArray()
        history.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("category", item.category)
                put("scannedAt", item.scannedAt)
                put("leafPoints", item.leafPoints)
                put("confirmed", item.confirmed)
            }
            array.put(obj)
        }
        preferences.edit().putString(KEY_HISTORY, array.toString()).apply()
    }

    private companion object {
        private const val PREF_NAME = "scan_history_prefs"
        private const val KEY_HISTORY = "scan_history"
        private val lock = Any()
    }
}