package com.fpinkotlin.advancedtrees.exercise04

import com.fpinkotlin.advancedtrees.common.List
import com.fpinkotlin.advancedtrees.common.Result
import com.fpinkotlin.advancedtrees.common.getOrElse

class Map<out K: Comparable<@UnsafeVariance K>, V>(private val delegate: Tree<MapEntry<Int, List<Pair<K, V>>>> = Tree()) {

//    operator fun plus(entry: Pair<@UnsafeVariance K, V>): Map<K, V> = Map(delegate + MapEntry(entry))
//
//    operator fun minus(key: @UnsafeVariance K): Map<K, V> = Map(delegate - MapEntry(key))
//
//    fun contains(key: @UnsafeVariance K): Boolean = delegate.contains(MapEntry(key))
//
//    fun get(key: @UnsafeVariance K): Result<MapEntry<@UnsafeVariance K, V>> = delegate[MapEntry(key)]

    fun getAll(key: @UnsafeVariance K): Result<List<Pair<K, V>>> {
        return delegate[MapEntry(key.hashCode())]
                .flatMap { x ->
                    x.value.map { lt ->
                        lt.map { t -> t }
                    }
                }
    }

    operator fun plus(entry: Pair<@UnsafeVariance K, V>): Map<K, V> {
        val list = getAll(entry.first).map { lt ->
            lt.foldLeft(List(entry)) { lst ->
                { pair ->
                    if (pair.first == entry.first) lst else lst.cons(pair)
                }
            }
        }.getOrElse { List(entry) }
        return Map(delegate + MapEntry.of(entry.first.hashCode(), list))
    }

    operator fun minus(key: @UnsafeVariance K): Map<K, V> {
        val list = getAll(key).map { lt ->
            lt.foldLeft(List()) { lst: List<Pair<K, V>> ->
                { pair ->
                    if (pair.first == key) lst else lst.cons(pair)
                }
            }
        }.getOrElse { List() }
        return when {
            list.isEmpty() -> Map(delegate - MapEntry(key.hashCode()))
            else -> Map(delegate + MapEntry.of(key.hashCode(), list))
        }
    }

    fun contains(key: @UnsafeVariance K): Boolean =
            getAll(key).map { list ->
                list.exists { pair ->
                    pair.first == key
                }
            }.getOrElse( false)

    fun get(key: @UnsafeVariance K): Result<Pair<K, V>> =
            getAll(key).flatMap { list ->
                list.filter { pair ->
                    pair.first == key
                }.headSafe()
            }

    fun isEmpty(): Boolean = delegate.isEmpty()

//    fun <B> foldLeft(identity: B, f: (B) -> (MapEntry<@UnsafeVariance K, V>) -> B, g: (B) -> (B) -> B): B =
//            delegate.foldLeft(identity, { b ->
//                { me: MapEntry<K, V> ->
//                    f(b)(me)
//                }
//            }, g)

//    fun values(): List<V> =
//        sequence(delegate.foldInReverseOrder(List<Result<V>>()) { lst1 ->
//            { me ->
//                { lst2 ->
//                    lst2.concat(lst1.cons(me.value))
//                }
//            }
//        }).getOrElse(List())

    companion object {

        operator fun <K: Comparable<@UnsafeVariance K>, V> invoke(): Map<K, V> = Map()
    }
}
