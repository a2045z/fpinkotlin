package com.fpinkotlin.advancedtrees.exercise09

import com.fpinkotlin.advancedtrees.common.List
import com.fpinkotlin.advancedtrees.common.Option
import com.fpinkotlin.advancedtrees.common.Result
import com.fpinkotlin.advancedtrees.common.getOrElse


sealed class Heap<out A: Comparable<@UnsafeVariance A>> {

    internal abstract fun left(): Result<Heap<A>>

    internal abstract fun right(): Result<Heap<A>>

    internal abstract fun rank(): Int

    abstract fun head(): Result<A>

    abstract fun length(): Int

    abstract fun isEmpty(): Boolean

    operator fun plus(element: @UnsafeVariance A): Heap<A> = merge(this, Heap(element))

    abstract fun tail(): Result<Heap<A>>

    abstract fun get(index: Int): Result<A>

    abstract fun pop(): Option<Pair<A, Heap<A>>>

    fun toList(): List<A> = foldLeft(List<A>()) { list -> { a -> list.cons(a) } }.reverse()

    fun <B> foldLeft(identity: B, f: (B) -> (A) -> B): B = unfold(this, { it.pop() }, identity, f)

    fun <A, S, B> unfold(z: S, getNext: (S) -> Option<Pair<A, S>>, identity: B, f: (B) -> (A) -> B): B {
        tailrec fun unfold(acc: B, z: S): B {
            val next = getNext(z)
            return when (next) {
                is Option.None -> acc
                is Option.Some ->
                    unfold(f(acc)(next.value.first), next.value.second)
            }
        }
        return unfold(identity, z)
    }

    internal object Empty: Heap<Nothing>() {

        override fun pop(): Option<Pair<Nothing, Heap<Nothing>>> = Option()

        override fun get(index: Int): Result<Nothing> = Result.failure(NoSuchElementException("Index out of bounds"))

        override fun tail(): Result<Heap<Nothing>> = Result.failure(IllegalStateException("tail() called on empty heap"))

        override fun left(): Result<Heap<Nothing>> = Result(Empty)

        override fun right(): Result<Heap<Nothing>> = Result(Empty)

        override fun rank(): Int = 0

        override fun head(): Result<Nothing> =
                Result.failure(NoSuchElementException("head() called on empty heap"))

        override fun length(): Int = 0

        override fun isEmpty(): Boolean = true
    }

    internal class H<out A: Comparable<@UnsafeVariance A>>(internal val length: Int,
                                                           internal val rank: Int,
                                                           internal val left: Heap<A>,
                                                           internal val head: A,
                                                           internal val right: Heap<A>): Heap<A>()  {

        override fun pop(): Option<Pair<A, Heap<A>>> = Option(Pair(head, merge(left, right)))

        override fun get(index: Int): Result<A> = when (index) {
            0 -> Result(head)
            else -> tail().flatMap { it.get(index - 1) }
        }

        override fun tail(): Result<Heap<A>> = Result(merge(left, right))

        override fun left(): Result<Heap<A>> = Result(left)

        override fun right(): Result<Heap<A>> = Result(right)

        override fun rank(): Int = rank

        override fun head(): Result<A> = Result(head)

        override fun length(): Int = length

        override fun isEmpty(): Boolean = false
    }

    companion object {

        operator fun <A: Comparable<A>> invoke(): Heap<A> = Empty

        operator fun <A: Comparable<A>> invoke(element: A): Heap<A> = H(1, 1, Empty, element, Empty)

        protected fun <A : Comparable<A>> merge(head: A, first: Heap<A>, second: Heap<A>): Heap<A> =
            when {
                first.rank() >= second.rank() -> H(first.length() + second.length() + 1,
                        second.rank() + 1, first, head, second)
                else -> H(first.length() + second.length() + 1,
                        first.rank() + 1, second, head, first)
            }

        fun <A: Comparable<A>> merge(first: Heap<A>, second: Heap<A>): Heap<A> =
                first.head().flatMap { fh ->
                    second.head().flatMap { sh ->
                        when {
                            fh <= sh -> first.left().flatMap { fl ->
                                first.right().map { fr ->
                                    merge(fh, fl, merge(fr, second))
                                }
                            }
                            else -> second.left().flatMap { sl ->
                                second.right().map { sr ->
                                    merge(sh, sl, merge(first, sr))
                                }
                            }
                        }
                    }
                }.getOrElse(when (first) {
                    is Empty -> second
                    else -> first
                })
    }

}