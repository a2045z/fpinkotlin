package com.fpinkotlin.workingwithlaziness.exercise13

import com.fpinkotlin.common.getOrElse
import com.fpinkotlin.generators.IntGenerator
import com.fpinkotlin.generators.forAll
import io.kotlintest.specs.StringSpec

class LazyTest: StringSpec() {

    init {

        "dropAtMost&takeAtMost" {
            forAll(IntGenerator(0, 100_000), { a ->
                val offset = IntGenerator(1, 50_000).generate()
                val stream = Stream.from(a).dropAtMost(offset).takeAtMost(offset)
                stream.head().map { it == a + offset }.getOrElse(false)
            })
        }
    }
}
