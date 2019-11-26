package com.malinskiy.marathon.cache

import kotlinx.coroutines.io.ByteWriteChannel
import kotlinx.coroutines.io.writeFully

class SimpleEntryWriter(data: String) : CacheEntryWriter {

    private val bytes = data.toByteArray()

    override val size: Long
        get() = bytes.size.toLong()

    override suspend fun writeTo(output: ByteWriteChannel) {
        output.writeFully(bytes)
    }

}
