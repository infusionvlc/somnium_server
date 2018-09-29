package com.infusionvlc.somniumserver.users

import com.infusionvlc.somniumserver.users.models.LoginRequest
import com.infusionvlc.somniumserver.users.models.RegisterRequest
import com.infusionvlc.somniumserver.users.models.toResponse
import com.infusionvlc.somniumserver.users.usecases.LoginUser
import com.infusionvlc.somniumserver.users.usecases.RegisterUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/v1")
class AuthenticationController(
  private val loginUser: LoginUser,
  private val registerUser: RegisterUser
) {

  @PostMapping("/login")
  fun login(@RequestBody request: LoginRequest): ResponseEntity<String> {
    val token = loginUser.execute(request)
    return ResponseEntity.ok(token)
  }

  @PostMapping("/register")
  fun register(@RequestBody request: RegisterRequest): ResponseEntity<*> {
    val user = registerUser.execute(request)
    return ResponseEntity(user.toResponse(), HttpStatus.CREATED)
  }

}
