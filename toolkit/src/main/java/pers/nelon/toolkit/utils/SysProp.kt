package pers.nelon.toolkit.utils

import android.annotation.SuppressLint
import java.lang.reflect.Method

object SysProp {
    private val systemProperties_get: Method by lazy {
        Class.forName("android.os.SystemProperties").getMethod("get", String::class.java)
    }
    private val systemProperties_set: Method by lazy {
        Class.forName("android.os.SystemProperties").getMethod("set", String::class.java, String::class.java)
    }

    @SuppressLint("PrivateApi", "MissingPermission")
    operator fun get(key: String, defValue: String): String {
        return try {
            val ret = (systemProperties_get.invoke(null, key) ?: defValue) as String
            if (ret.isNotEmpty()) {
                ret
            } else {
                defValue
            }
        } catch (th: Throwable) {
            defValue
        }
    }

    /**
     * 根据给定的key和值设置属性, 该方法需要特定的权限才能操作.
     */
    @SuppressLint("PrivateApi", "MissingPermission")
    operator fun set(key: String, value: String) {
        try {
            systemProperties_set.invoke(Class.forName("android.os.SystemProperties"), key, value)
        } catch (th: Throwable) {
            th.printStackTrace()
        }
    }
}