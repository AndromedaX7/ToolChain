package c.feature.extension

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

operator fun ContentValues.set(name: String, value: String?) {
    put(name, value)
}

operator fun ContentValues.set(name: String, value: Float?) {
    put(name, value)
}


operator fun ContentValues.set(name: String, value: Double?) {
    put(name, value)
}

operator fun ContentValues.set(name: String, value: Int?) {
    put(name, value)
}


operator fun ContentValues.set(name: String, value: Long?) {
    put(name, value)
}


operator fun Intent.set(name: String, value: Int?): Intent {
    putExtra(name, value)
    return this
}

operator fun Intent.set(name: String, value: Float?): Intent {
    putExtra(name, value)
    return this
}

operator fun Intent.set(name: String, value: Long?): Intent {
    putExtra(name, value)
    return this
}

operator fun Intent.set(name: String, value: Double?): Intent {
    putExtra(name, value)
    return this
}

operator fun Intent.set(name: String, value: String): Intent {
    putExtra(name, value)
    return this
}
operator fun Intent.set(name: String, value: Boolean?): Intent {
    putExtra(name, value)
    return this
}
inline operator fun <reified T : Any> Intent.get(name: String, clazz: Class<T>): T? {
    return when (clazz) {
        String::class.java -> getStringExtra(name) as T
        Byte::class.java -> getByteExtra(name, 0) as T
        Short::class.java -> getShortExtra(name, 0) as T
        Int::class.java -> getIntExtra(name, 0) as T
        Long::class.java -> getLongExtra(name, 0) as T
        Float::class.java -> getFloatExtra(name, 0f) as T
        Double::class.java -> getDoubleExtra(name, 0.0) as T
        Char::class.java -> getCharExtra(name, '\u0000') as T
        Serializable::class.java -> getSerializableExtra(name) as T
        Boolean::class.java -> getBooleanExtra(name, false) as T
        Bundle::class.java -> getBundleExtra(name) as T
        else -> null
    }
}

//operator fun <T : Parcelable> Intent.get(name: String): T {
//    return getParcelableExtra<T>(name)
//}

//operator fun <T : Parcelable> Intent.get(name: String, array: Boolean): ArrayList<Parcelable> {
//    val result = ArrayList<Parcelable>()
//    if (array) {
//        val target = getParcelableArrayExtra(name)
//        for (i in 0..target.size) {
//            result.add(target[i])
//        }
//
//    } else {
//        val target = getParcelableArrayListExtra<T>(name)
//        for (i in 0..target.size) {
//            result.add(target[i])
//        }
//    }
//    return result
//}
