
package com.turbosongs

import android.os.Bundle

class BundlePair(
        private val block: (Bundle) -> Unit
) {
    fun apply(bundle: Bundle) = block(bundle)
}

inline fun bundle(initFun: Bundle.() -> Unit) = Bundle().apply(initFun)
inline fun bundleOf(initFun: (Bundle) -> Unit) =
        Bundle().also(initFun)

fun bundleOf(vararg pairs: BundlePair) = bundle {
    pairs.forEach { it.apply(this) }
}

infix fun String.bundleTo(value: Boolean) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: Byte) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: Short) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: Int) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: Long) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: Float) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: Double) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: Char) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: CharSequence) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: String) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: Bundle) = BundlePair { it[this] = value }
/*infix fun String.bundleTo(value: Parcelable) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: Serializable) = BundlePair { it[this] = value }*/

/*@RequiresApi(18)
infix fun String.bundleTo(value: Binder) = BundlePair { it[this] = value }
@RequiresApi(21)
infix fun String.bundleTo(value: Size) = BundlePair { it[this] = value }
@RequiresApi(21)
infix fun String.bundleTo(value: SizeF) = BundlePair { it[this] = value }*/

infix fun String.bundleTo(value: BooleanArray) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: ByteArray) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: ShortArray) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: IntArray) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: LongArray) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: FloatArray) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: DoubleArray) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: CharArray) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: Array<out CharSequence>) = BundlePair { it[this] = value }
infix fun String.bundleTo(value: Array<out String>) = BundlePair { it[this] = value }
//infix fun String.bundleTo(value: Array<out Parcelable>) = BundlePair { it[this] = value }

//infix fun String.bundleTo(value: SparseArray<out Parcelable>) = BundlePair { it[this] = value }

//for null
infix fun String.bundleTo(n: Void?) = BundlePair { it[this] = null }


operator fun Bundle.set(key: String, value: Boolean) = putBoolean(key, value)
operator fun Bundle.set(key: String, value: Byte) = putByte(key, value)
operator fun Bundle.set(key: String, value: Short) = putShort(key, value)
operator fun Bundle.set(key: String, value: Int) = putInt(key, value)
operator fun Bundle.set(key: String, value: Long) = putLong(key, value)
operator fun Bundle.set(key: String, value: Float) = putFloat(key, value)
operator fun Bundle.set(key: String, value: Double) = putDouble(key, value)
operator fun Bundle.set(key: String, value: Char) = putChar(key, value)
operator fun Bundle.set(key: String, value: CharSequence) = putCharSequence(key, value)
operator fun Bundle.set(key: String, value: String) = putString(key, value)
operator fun Bundle.set(key: String, value: Bundle) = putBundle(key, value)
/*operator fun Bundle.set(key: String, value: Parcelable) = putParcelable(key, value)
operator fun Bundle.set(key: String, value: Serializable) = putSerializable(key, value)*/

/*@RequiresApi(18)
operator fun Bundle.set(key: String, value: Binder) = putBinder(key, value)
@RequiresApi(21)
operator fun Bundle.set(key: String, value: Size) = putSize(key, value)
@RequiresApi(21)
operator fun Bundle.set(key: String, value: SizeF) = putSizeF(key, value)*/

operator fun Bundle.set(key: String, value: BooleanArray) = putBooleanArray(key, value)
operator fun Bundle.set(key: String, value: ByteArray) = putByteArray(key, value)
operator fun Bundle.set(key: String, value: ShortArray) = putShortArray(key, value)
operator fun Bundle.set(key: String, value: IntArray) = putIntArray(key, value)
operator fun Bundle.set(key: String, value: LongArray) = putLongArray(key, value)
operator fun Bundle.set(key: String, value: FloatArray) = putFloatArray(key, value)
operator fun Bundle.set(key: String, value: DoubleArray) = putDoubleArray(key, value)
operator fun Bundle.set(key: String, value: CharArray) = putCharArray(key, value)
operator fun Bundle.set(key: String, value: Array<out CharSequence>) = putCharSequenceArray(key, value)
operator fun Bundle.set(key: String, value: Array<out String>) = putStringArray(key, value)
//operator fun Bundle.set(key: String, value: Array<out Parcelable>) = putParcelableArray(key, value)

//operator fun Bundle.set(key: String, value: SparseArray<out Parcelable>) = putSparseParcelableArray(key, value)

//for null
operator fun Bundle.set(key: String, value: Void?) = putString(key, null)
