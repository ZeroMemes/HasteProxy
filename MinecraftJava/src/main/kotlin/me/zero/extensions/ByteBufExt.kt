/*
 * This file is part of HasteProxy.
 *
 * HasteProxy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HasteProxy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HasteProxy.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.zero.extensions

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.EncoderException
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * @author Brady
 * @since 8/15/2018
 */

/**
 * Reads a length-prefixed byte array from the buffer
 */
internal fun ByteBuf.readByteArray(): ByteArray {
    return this.readByteArray(this.readableBytes())
}

/**
 * Reads a length-prefixed byte array from the buffer
 *
 * @param maxLength The maximum length of the byte array
 */
internal fun ByteBuf.readByteArray(maxLength: Int): ByteArray {
    val length = this.readVarInt()

    if (length > maxLength) {
        throw DecoderException("ByteArray with size $length is bigger than allowed $maxLength")
    } else {
        val bytes = ByteArray(length)
        this.readBytes(bytes)
        return bytes
    }
}

/**
 * Writes a length-prefixed byte array to the buffer
 *
 * @param array The byte array
 */
internal fun ByteBuf.writeByteArray(array: ByteArray): ByteBuf {
    return this.writeVarInt(array.size).writeBytes(array)
}

/**
 * Reads a length-prefixed compressed int array from the buffer and
 * returns an uncompressed int array.
 */
internal fun ByteBuf.readVarIntArray(): IntArray {
    return this.readVarIntArray(this.readableBytes())
}

/**
 * Reads a length-prefixed compressed int array from the buffer and
 * returns an uncompressed int array.
 *
 * @param maxLength The maximum length of the byte array
 */
internal fun ByteBuf.readVarIntArray(maxLength: Int): IntArray {
    val length = this.readVarInt()

    if (length > maxLength) {
        throw DecoderException("VarIntArray with size $length is bigger than allowed $maxLength")
    } else {
        return IntArray(length) {
            this.readVarInt()
        }
    }
}

/**
 * Writes a length-prefixed compressed int array to the buffer
 *
 * @param array The uncompressed int array
 */
internal fun ByteBuf.writeVarIntArray(array: IntArray): ByteBuf {
    this.writeVarInt(array.size)

    for (i in array)
        this.writeVarInt(i)

    return this
}

/**
 * Reads a length-prefixed array of longs from the buffer
 */
internal fun ByteBuf.readLongArray(): LongArray {
    return this.readLongArray(this.readableBytes() / 8)
}

/**
 * Reads a length-prefixed array of longs from the buffer
 *
 * @param maxLength The maximum length of the array
 */
internal fun ByteBuf.readLongArray(maxLength: Int): LongArray {
    val length = this.readVarInt()

    if (length > maxLength) {
        throw DecoderException("LongArray with size $length is bigger than allowed $maxLength")
    } else {
        return LongArray(length) {
            this.readLong()
        }
    }
}

/**
 * Writes a length-prefixed array of longs to the buffer
 *
 * @param array The uncompressed int array
 */
internal fun ByteBuf.writeLongArray(array: LongArray): ByteBuf {
    this.writeVarInt(array.size)

    for (i in array)
        this.writeLong(i)

    return this
}

/**
 * Reads an Enum value from the buffer
 *
 * @param type The enum type
 */
internal fun <T : Enum<T>> ByteBuf.readEnumValue(type: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return (type.enumConstants as Array<*>)[this.readVarInt()] as T
}

/**
 * Writes an enum value to the buffer
 *
 * @param value The enum value
 */
internal fun ByteBuf.writeEnumValue(value: Enum<*>): ByteBuf {
    return this.writeVarInt(value.ordinal)
}

/**
 * Reads a compressed int from the buffer and returns the uncompressed value
 */
internal fun ByteBuf.readVarInt(): Int {
    var i = 0
    var j = 0

    while (true) {
        val b0 = this.readByte().toInt()
        i = i or (b0 and 127 shl j++ * 7)

        if (j > 5) {
            throw RuntimeException("VarInt too big")
        }

        if (b0 and 128 != 128) {
            break
        }
    }

    return i
}

/**
 * Writes a compressed int to the buffer.
 *
 * @param input The uncompressed int value
 */
internal fun ByteBuf.writeVarInt(input: Int): ByteBuf {

    // Kotlin doesn't like mutable method parameters :(
    var i = input

    while (i and -128 != 0) {
        this.writeByte(i and 127 or 128)
        i = i ushr 7
    }

    return this.writeByte(input)
}

/**
 * Reads a compressed long from the buffer and returns the uncompressed value
 */
internal fun ByteBuf.readVarLong(): Long {
    var i = 0L
    var j = 0

    while (true) {
        val b0 = this.readByte().toLong()
        i = i or (b0 and 127 shl j++ * 7)

        if (j > 10) {
            throw RuntimeException("VarLong too big")
        }

        if (b0 and 128 != 128L) {
            break
        }
    }

    return i
}

/**
 * Writes a compressed long to the buffer.
 *
 * @param input The uncompressed long value
 */
internal fun ByteBuf.writeVarLong(input: Long): ByteBuf {

    // Kotlin doesn't like mutable method parameters :(
    var i = input

    while (i and -128 != 0L) {
        this.writeByte((i and 127L or 128).toInt())
        i = i ushr 7
    }

    return this.writeByte(input.toInt())
}

/**
 * Reads a UUID from the buffer
 */
internal fun ByteBuf.readUniqueId(): UUID {
    return UUID(this.readLong(), this.readLong())
}

/**
 * Writes a UUID to the buffer
 *
 * @param uuid The UUID
 */
internal fun ByteBuf.writeUniqueId(uuid: UUID) {
    this.writeLong(uuid.mostSignificantBits)
    this.writeLong(uuid.leastSignificantBits)
}

/**
 * Reads a string from this buffer
 *
 * @param maxLength The maximum length of the string
 */
internal fun ByteBuf.readString(maxLength: Int): String {
    val length = this.readVarInt()

    if (length > maxLength * 4) {
        throw DecoderException("The received encoded string buffer length is longer than maximum allowed ($length > " + (maxLength * 4) + ")")
    } else if (length < 0) {
        throw DecoderException("The received encoded string buffer length is less than zero! Weird string!")
    } else {
        // Read the string
        val read = this.toString(this.readerIndex(), length, StandardCharsets.UTF_8)

        // Move the reader index
        this.readerIndex(this.readerIndex() + length)

        if (read.length > maxLength) {
            throw DecoderException("The received string length is longer than maximum allowed ($length > $maxLength)")
        } else {
            return read
        }
    }
}

/**
 * Writes a string to this buffer
 *
 * @param string The string
 */
internal fun ByteBuf.writeString(string: String): ByteBuf {
    val bytes = string.toByteArray(StandardCharsets.UTF_8)

    if (bytes.size > 0x7FFF) {
        throw EncoderException("String too big (was " + bytes.size + " bytes encoded, max " + 0x7FFF + ")")
    } else {
        return this.writeVarInt(bytes.size).writeBytes(bytes)
    }
}

/**
 * Reads a date from the buffer
 */
internal fun ByteBuf.readDate(): Date {
    return Date(this.readLong())
}

/**
 * Writes a date to the buffer
 *
 * @param date The date
 */
internal fun ByteBuf.writeDate(date: Date): ByteBuf {
    return this.writeLong(date.time)
}