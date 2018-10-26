package com.infusionvlc.somniumserver.users

import com.infusionvlc.somniumserver.users.security.models.SecurityUser
import com.infusionvlc.somniumserver.users.usecases.FollowUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore
import javax.websocket.server.PathParam

@RestController
@RequestMapping("/users/v1")
class UsersController(
  private val followUser: FollowUser
) {

  @PostMapping("/follow/{id}")
  fun followUser(
    @PathParam("id") id: Long,
    @ApiIgnore authentication: Authentication
  ): ResponseEntity<*> {
    val requestUser = authentication.principal as SecurityUser

    return followUser.execute(id, requestUser.id)
      .fold(
        {
          when (it) {
            is FollowUser.Errors.PersistenceError -> ResponseEntity("", HttpStatus.INTERNAL_SERVER_ERROR)
            is FollowUser.Errors.UserNotFound -> ResponseEntity("", HttpStatus.NOT_FOUND)
            else -> ResponseEntity("", HttpStatus.BAD_REQUEST)
          }
        },
        {
          ResponseEntity.ok(it)
        }
      )
  }
}
