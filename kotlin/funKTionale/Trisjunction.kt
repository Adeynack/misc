package com.github.adeynack.finances.service.util

import org.funktionale.collections.prependTo
import org.funktionale.either.Disjunction
import org.funktionale.either.Either
import org.funktionale.either.EitherLike
import org.funktionale.either.LeftLike
import org.funktionale.either.RightLike
import org.funktionale.option.Option
import org.funktionale.option.getOrElse
import org.funktionale.option.toOption
import org.funktionale.tries.Try
import org.funktionale.utils.hashCodeForNullable
import java.util.NoSuchElementException


// todo: Create PR to FunKTionale to propose this
sealed class Trisjunction<out L, out R> : EitherLike {

    abstract operator fun component1(): L?
    abstract operator fun component2(): R?

    companion object {

        inline operator fun <L, R> invoke(body: () -> Either<L, R>): Trisjunction<L, R> =
            try {
                val v = body()
                when (v) {
                    is Either.Left -> Trisjunction.Left(v.l)
                    is Either.Right -> Trisjunction.Right(v.r)
                }
            } catch (t: Throwable) {
                Trisjunction.Failure(t)
            }

    }

    /**
     * Gets a failure projection, as an [Option].
     */
    fun failure(): Option<Throwable> = when (this) {
        is Failure -> throwable.toOption()
        else -> Option.empty()
    }

    /**
     * Perform a side effect if the current [Trisjunction] is [Left].
     * @f the function to execute on the left value.
     * @return self, for fluent chaining.
     */
    fun onLeft(f: (L) -> Unit): Trisjunction<L, R> {
        when (this) {
            is Left -> f(value)
        }
        return this
    }

    /**
     * Perform a side effect if the current [Trisjunction] is [Right].
     * @f the function to execute on the right value.
     * @return self, for fluent chaining.
     */
    fun onRight(f: (R) -> Unit): Trisjunction<L, R> {
        when (this) {
            is Right -> f(value)
        }
        return this
    }

    /**
     * Perform a side effect if the current [Trisjunction] is [Failure].
     * @f the function to execute on the failure.
     * @return self, for fluent chaining.
     */
    fun onFailure(f: (Throwable) -> Unit): Trisjunction<L, R> {
        when (this) {
            is Failure -> f(throwable)
        }
        return this
    }

    /**
     * Right becomes Left.
     * Left becomes Right.
     * Failure stays a Failure.
     */
    fun swap(): Trisjunction<R, L> = when (this) {
        is Right -> Left(value)
        is Left -> Right(value)
        is Failure -> Failure(throwable)
    }

    fun <X> fold(fl: (L) -> X, fr: (R) -> X): X = when (this) {
        is Right -> fr(value)
        is Left -> fl(value)
        is Failure -> throw IllegalStateException("Cannot fold on a failed Trisjunction", throwable)
    }

    fun get(): R = when (this) {
        is Right -> value
        is Left -> throw NoSuchElementException("Trisjunction.Left")
        is Failure -> throw IllegalStateException("Cannot get the value of a failed Trisjunction", throwable)
    }

    fun forEach(f: (R) -> Unit) {
        when (this) {
            is Right -> f(value)
        }
    }

    fun exists(predicate: (R) -> Boolean): Boolean = when (this) {
        is Right -> predicate(value)
        else -> false
    }

    fun <X> map(f: (R) -> X): Trisjunction<L, X> = when (this) {
        is Right -> Right(f(value))
        is Left -> Left(value)
        is Failure -> Failure(throwable)
    }

    fun filter(predicate: (R) -> Boolean): Option<Trisjunction<L, R>> = when (this) {
        is Right ->
            if (predicate(value)) {
                Option.Some(this)
            } else {
                Option.None
            }
        else -> Option.None
    }

    fun toList(): List<R> = when (this) {
        is Right -> listOf(value)
        else -> listOf()
    }

    fun toOption(): Option<R> = when (this) {
        is Right -> Option.Some(value)
        else -> Option.None
    }

    fun toEither(): Either<Either<Throwable, L>, R> = when (this) {
        is Right -> Either.Right(value)
        is Left -> Either.Left(Either.Right(value))
        is Failure -> Either.Left(Either.Left(throwable))
    }

    fun toDisjunction(): Disjunction<Either<Throwable, L>, R> = when (this) {
        is Right -> Disjunction.Right(value)
        is Left -> Disjunction.Left(Either.Right(value))
        is Failure -> Disjunction.Left(Either.Left(throwable))
    }

    class Left<out L, out R>(val value: L) : Trisjunction<L, R>(), LeftLike {
        override fun component1(): L? = value
        override fun component2(): R? = null
        override fun equals(other: Any?): Boolean = when (other) {
            is Left<*, *> -> value == other.value
            else -> false
        }

        override fun hashCode(): Int = value.hashCodeForNullable(43) { a, b -> a * b }
        override fun toString(): String = "Trisjunction.Left($value)"
    }

    class Right<out L, out R>(val value: R) : Trisjunction<L, R>(), RightLike {
        override fun component1(): L? = null
        override fun component2(): R? = value
        override fun equals(other: Any?): Boolean = when (other) {
            is Right<*, *> -> value == other.value
            else -> false
        }

        override fun hashCode(): Int = value.hashCodeForNullable(43) { a, b -> a * b }
        override fun toString(): String = "Trisjunction.Right($value)"
    }

    class Failure<L, R>(val throwable: Throwable) : Trisjunction<L, R>() {
        override fun isLeft(): Boolean = false
        override fun isRight(): Boolean = false
        override fun component1(): L? = null
        override fun component2(): R? = null
        override fun equals(other: Any?): Boolean = when (other) {
            this -> true
            is Failure<*, *> -> throwable == other.throwable
            else -> false
        }

        override fun hashCode(): Int = throwable.hashCodeForNullable(43) { a, b -> a * b }
        override fun toString(): String = "Trisjunction.Failure($throwable)"
    }

}

/**
 * Right becomes Left.
 * Left becomes Right of Success of Left.
 * Failure becomes Right of its cause (throwable).
 *
 * Typical use case: Having the two possible cause -- an failure (`Throwable`) or a known
 * cause (of type [L]) -- on the right side of a disjunction.
 */
fun <L, R> Trisjunction<L, R>.flip(): Disjunction<R, Try<L>> = when (this) {
    is Trisjunction.Right -> Disjunction.Left(value)
    is Trisjunction.Left -> Disjunction.Right(Try.Success(value))
    is Trisjunction.Failure -> Disjunction.Right(Try.Failure(throwable))
}

fun <T> Trisjunction<T, T>.merge(): Try<T> = when (this) {
    is Trisjunction.Left -> Try.Success(value)
    is Trisjunction.Right -> Try.Success(value)
    is Trisjunction.Failure -> Try.Failure(throwable)
}

fun <L, R> Trisjunction<L, R>.getOrElse(default: () -> R): R = when (this) {
    is Trisjunction.Right -> value
    else -> default()
}

fun <L, R> Trisjunction<L, R>.getOrElse(default: R): R = when (this) {
    is Trisjunction.Right -> value
    else -> default
}

fun <X, L, R> Trisjunction<L, R>.flatMap(f: (R) -> Trisjunction<L, X>): Trisjunction<L, X> = when (this) {
    is Trisjunction.Right ->
        try {
            f(value)
        } catch (t: Throwable) {
            Trisjunction.Failure<L, X>(t)
        }
    is Trisjunction.Left -> Trisjunction.Left(value)
    is Trisjunction.Failure -> Trisjunction.Failure(throwable)
}

fun <L, R, X, Y> Trisjunction<L, R>.map(x: Trisjunction<L, X>, f: (R, X) -> Y): Trisjunction<L, Y> =
    flatMap { r ->
        x.map { xx -> f(r, xx) }
    }

fun <T, L, R> List<T>.trisjunctionTraverse(f: (T) -> Trisjunction<L, R>): Trisjunction<L, List<R>> =
    foldRight(Trisjunction.Right(emptyList())) { i: T, accumulator: Trisjunction<L, List<R>> ->
        val trisjunction = f(i)
        when (trisjunction) {
            is Trisjunction.Right -> trisjunction.map(accumulator) { head: R, tail: List<R> ->
                head prependTo tail
            }
            is Trisjunction.Left -> Trisjunction.Left(trisjunction.value)
            is Trisjunction.Failure -> Trisjunction.Failure(trisjunction.throwable)
        }
    }

fun <L, R> List<Trisjunction<L, R>>.trisjunctionSequential(): Trisjunction<L, List<R>> = trisjunctionTraverse { it }

fun <L, R> Either<L, R>.toTrisjunction(): Trisjunction<L, R> = when (this) {
    is Either.Left -> Trisjunction.Left(l)
    is Either.Right -> Trisjunction.Right(r)
}

fun <L, R> Disjunction<L, R>.toTrisjunction(): Trisjunction<L, R> = when (this) {
    is Disjunction.Left -> Trisjunction.Left(value)
    is Disjunction.Right -> Trisjunction.Right(value)
}

inline fun <L, R> R?.toTrisjunctionRight(crossinline left: () -> L): Trisjunction<L, R> =
    if (this == null)
        try {
            Trisjunction.Left<L, R>(left())
        } catch (t: Throwable) {
            Trisjunction.Failure<L, R>(t)
        }
    else Trisjunction.Right(this)

inline fun <L, R> L?.toTrisjunctionLeft(crossinline right: () -> R): Trisjunction<L, R> =
    if (this == null)
        try {
            Trisjunction.Right<L, R>(right())
        } catch (t: Throwable) {
            Trisjunction.Failure<L, R>(t)
        }
    else Trisjunction.Left(this)

inline fun <L, R> Option<R>.toTrisjunctionRight(crossinline left: () -> L): Trisjunction<L, R> =
    map { Trisjunction.Right<L, R>(it) }.getOrElse {
        try {
            Trisjunction.Left(left())
        } catch (t: Throwable) {
            Trisjunction.Failure(t)
        }
    }

inline fun <L, R> Option<R>.toTrisjunctionLeft(crossinline right: () -> L): Trisjunction<R, L> =
    map { Trisjunction.Left<R, L>(it) }.getOrElse {
        try {
            Trisjunction.Right(right())
        } catch (t: Throwable) {
            Trisjunction.Failure(t)
        }
    }

fun <L, R> Try<R>.toTrisjunctionRight(): Trisjunction<L, R> = when (this) {
    is Try.Success -> Trisjunction.Right(this.get())
    is Try.Failure -> Trisjunction.Failure(throwable)
}

inline fun <L, R> Try<R>.toTrisjunctionRight(crossinline left: (Throwable) -> Try<L>): Trisjunction<L, R> = when (this) {
    is Try.Success -> Trisjunction.Right(this.get())
    is Try.Failure -> {
        val l = left(throwable)
        when (l) {
            is Try.Success -> Trisjunction.Left(l.get())
            is Try.Failure -> Trisjunction.Failure(l.throwable)
        }
    }
}

fun <L, R> Try<L>.toTrisjunctionLeft(): Trisjunction<L, R> = when (this) {
    is Try.Success -> Trisjunction.Left(this.get())
    is Try.Failure -> Trisjunction.Failure(throwable)
}

inline fun <L, R> Try<L>.toTrisjunctionLeft(crossinline right: (Throwable) -> Try<R>): Trisjunction<L, R> = when (this) {
    is Try.Success -> Trisjunction.Left(this.get())
    is Try.Failure -> {
        val r = right(throwable)
        when (r) {
            is Try.Success -> Trisjunction.Right(r.get())
            is Try.Failure -> Trisjunction.Failure(throwable)
        }
    }
}
