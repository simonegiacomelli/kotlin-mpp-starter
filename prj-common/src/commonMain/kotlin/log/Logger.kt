package log

import kotlin.reflect.KClass


fun logger(kClass: KClass<out Any>): Logger = loggerFactory(kClass.simpleName ?: "simpleName-null")
inline fun <reified T> T.logger(): Logger = loggerFactory(T::class.simpleName ?: "simpleName-null")

fun loggerFactory(name: String): Logger = Logger(name)


class Logger(val fullName: String) {
    fun info(msg: String) = println("$fullName - $msg")
    inline fun i(msg: String) = info(msg)
}