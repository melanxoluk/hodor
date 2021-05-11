package com.melanxoluk.hodor.common


// way to receive npe is always exists, but use it like in example
// will help to use !! which could raise it

sealed class Result<T>

class Positive<T>(var body: T): Result<T>()

class Negative<T>(var exceptionMessage: String): Result<T>()


fun <T> positive(entity: T) = Positive(entity)

fun <T> negative(msg: String) = Negative<T>(msg)


fun example(res: Result<String>) {
    when(res) {
        is Positive -> {
            print(res.body)
        }
        is Negative -> {
            print(res.exceptionMessage)
        }
    }
}
