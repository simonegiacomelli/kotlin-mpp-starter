package rpc

import kotlin.reflect.KClass

fun nameOf(kclass: KClass<*>): String = kclass.simpleName ?: error("no class name")
