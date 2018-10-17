package com.infusionvlc.somniumserver.base

import arrow.core.Either
import arrow.core.Try

fun <L, R> Try<R>.toEither(f: (Throwable) -> L): Either<L, R> =
  toEither().mapLeft(f)
