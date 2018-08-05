package com.melanxoluk.hodor


// ~~~ varargs checks

fun all(vararg objs: Any?, f: (Any?) -> Boolean) = objs.all(f)

fun allNotNull(vararg objs: Any?) = all(*objs) { it != null }


// ~~~ assertions

fun checkNotNull(vararg objs: Any) {
    checkNotNull(*objs
        .map { it to it::javaClass.name }
        .toTypedArray())
}

fun checkNotNull(vararg objs: Pair<Any, String>) {
    objs.forEach { kotlin.checkNotNull(it) }
}