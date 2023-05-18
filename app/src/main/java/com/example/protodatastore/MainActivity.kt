package com.example.protodatastore

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"
private const val SAMPLE_PREFS_FILENAME = "simple.dimple"

private val Context.samplePreferencesStore: DataStore<SamplePreferences> by dataStore(
    fileName = SAMPLE_PREFS_FILENAME,
    serializer = SamplePreferencesSerializer,
    corruptionHandler = ReplaceFileCorruptionHandler(
        produceNewData = { SamplePreferences.getDefaultInstance() }
    )
)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startScenario()
    }

    private fun startScenario() {
        lifecycleScope.launch {
            val samplePreferencesDeferred = async { subscribeOnSamplePreferences() }
            createOrUpdateSamplePreferencesFlag(true)
            delay(1000)
            createOrUpdateSamplePreferencesRandomSetting(123)
            delay(1000)
            createOrUpdateSamplePreferenceWow(SamplePreferences.WowEnum.QUINCE)
            delay(1000)
            clearSamplePreferences()
            samplePreferencesDeferred.cancel()
        }
    }

    private fun printSamplePreferences(preferences: SamplePreferences) {
        val flag = preferences.flag
        val randomSetting = if (preferences.hasRandomSetting()) preferences.randomSetting else null
        val wow = preferences.wow.name
        Log.d(TAG, "{ flag: $flag, randomSetting: $randomSetting, wow: $wow }")
    }

    private suspend fun subscribeOnSamplePreferences() {
        samplePreferencesStore.data.collect { samplePreferences: SamplePreferences ->
            printSamplePreferences(samplePreferences)
        }
    }

    private suspend fun getSamplePreferences(): SamplePreferences {
        return samplePreferencesStore.data.first()
    }

    private suspend fun createOrUpdateSamplePreferencesFlag(flag: Boolean) {
        samplePreferencesStore.updateData { preferences: SamplePreferences ->
            preferences.toBuilder()
                .setFlag(flag)
                .build()
        }
    }

    private suspend fun createOrUpdateSamplePreferencesRandomSetting(randomSetting: Int) {
        samplePreferencesStore.updateData { preferences: SamplePreferences ->
            preferences.toBuilder()
                .setRandomSetting(randomSetting)
                .build()
        }
    }

    private suspend fun createOrUpdateSamplePreferenceWow(wow: SamplePreferences.WowEnum) {
        samplePreferencesStore.updateData { preferences: SamplePreferences ->
            preferences.toBuilder()
                .setWow(wow)
                .build()
        }
    }

    private suspend fun clearSamplePreferences() {
        samplePreferencesStore.updateData { preferences: SamplePreferences ->
            preferences.toBuilder()
                .clear()
                .build()
        }
    }
}
