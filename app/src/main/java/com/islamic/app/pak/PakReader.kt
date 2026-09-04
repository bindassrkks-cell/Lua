package com.islamic.app.pak

import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.MessageDigest

data class PakEntry(
    val path: String, val contentType: Byte, val offset: Long,
    val size: Long, val compType: Byte, val sha256: ByteArray
)

class PakReader(val file: File) : AutoCloseable {
    private val raf = RandomAccessFile(file, "r")
    val entries = HashMap<String, PakEntry>()

    init {
        require(file.length() >= 64) { "Invalid PAK size." }
        raf.seek(0)
        val headerBuf = ByteArray(64)
        raf.readFully(headerBuf)
        val bb = ByteBuffer.wrap(headerBuf).order(ByteOrder.BIG_ENDIAN)

        val magic = ByteArray(4)
        bb.get(magic)
        if (String(magic, Charsets.US_ASCII) != "ISPK") throw SecurityException("Invalid PAK Magic")

        val fmtVer = bb.short
        val flags = bb.short
        val contentVersion = bb.int
        val fileCount = bb.int
        val indexOffset = bb.long
        val indexSize = bb.long
        val indexSha256 = ByteArray(32)
        bb.get(indexSha256)

        raf.seek(indexOffset)
        val indexBuf = ByteArray(indexSize.toInt())
        raf.readFully(indexBuf)

        val md = MessageDigest.getInstance("SHA-256")
        if (!md.digest(indexBuf).contentEquals(indexSha256)) throw SecurityException("Corrupted PAK index")

        val idxBb = ByteBuffer.wrap(indexBuf).order(ByteOrder.BIG_ENDIAN)
        for (i in 0 until fileCount) {
            val pathLen = idxBb.short.toInt() and 0xFFFF
            val pathBytes = ByteArray(pathLen)
            idxBb.get(pathBytes)
            val path = String(pathBytes, Charsets.UTF_8)
            val ctype = idxBb.get()
            val offset = idxBb.long
            val compSize = idxBb.long
            val origSize = idxBb.long
            val compType = idxBb.get()
            val fileSha = ByteArray(32)
            idxBb.get(fileSha)
            entries[path] = PakEntry(path, ctype, offset, compSize, compType, fileSha)
        }
    }

    fun exists(path: String) = entries.containsKey(path)

    @Synchronized
    fun readBytes(path: String): ByteArray {
        val entry = entries[path] ?: throw NoSuchElementException("Path not found: $path")
        raf.seek(entry.offset)
        val payload = ByteArray(entry.size.toInt())
        raf.readFully(payload)
        return payload
    }

    fun readText(path: String) = String(readBytes(path), Charsets.UTF_8)
    override fun close() = raf.close()
}
