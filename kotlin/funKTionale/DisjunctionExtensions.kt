package com.github.adeynack.finances.service.util

import org.funktionale.either.Disjunction

fun <L, R> R?.toDisjunctionRight(left: () -> L): Disjunction<L, R> = when (this) {
    null -> Disjunction.Left(left())
    else -> Disjunction.Right(this)
}
