package com.infusionvlc.somniumserver.users.models

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
object UserNotFoundError : RuntimeException("Bad username or password")
