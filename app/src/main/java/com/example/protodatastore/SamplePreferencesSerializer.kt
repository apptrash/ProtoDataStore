package com.example.protodatastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object SamplePreferencesSerializer : Serializer<SamplePreferences> {
    override val defaultValue: SamplePreferences = SamplePreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SamplePreferences {
        try {
            return SamplePreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: SamplePreferences, output: OutputStream) {
        t.writeTo(output)
    }
}
