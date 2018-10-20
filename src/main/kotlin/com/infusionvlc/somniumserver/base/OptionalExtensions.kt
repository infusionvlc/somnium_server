package com.infusionvlc.somniumserver.base

import arrow.core.Option
import java.util.Optional

fun <T> Optional<T>.toOption(): Option<T> = Option.fromNullable(this.orElseGet { null })
