package com.fpinkotlin.workingwithlaziness.exercise02


class Lazy<out A>(function: () -> A): () -> A {

    private val value: A by lazy(function)

    override operator fun invoke(): A = value

    companion object {

        operator fun <A> invoke(function: () -> A): Lazy<A> = Lazy(function)
    }
}

fun constructMessage(greetings: Lazy<String>, name: Lazy<String>): Lazy<String> = TODO("Implement this function")
