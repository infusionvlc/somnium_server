package com.infusionvlc.somniumserver.dreams

import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamCreationErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRequest
import com.infusionvlc.somniumserver.dreams.usecases.CreateDream
import com.infusionvlc.somniumserver.dreams.usecases.GetAllDreams
import com.infusionvlc.somniumserver.users.security.models.SecurityUser
import com.infusionvlc.somniumserver.users.usecases.FindUserByUsername
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dreams/v1")
class DreamController(
  private val getAllDreams: GetAllDreams,
  private val createDream: CreateDream,
  private val findUserByUsername: FindUserByUsername
) {

  @GetMapping("/")
  fun getAll(@RequestParam("page") page: Int,
             @RequestParam("page_size") pageSize: Int): ResponseEntity<List<Dream>> =
    ResponseEntity.ok(getAllDreams.execute(page, pageSize))

  @PostMapping("/")
  fun createDream(@RequestBody dreamRequest: DreamRequest,
                  authentication: Authentication): ResponseEntity<*> {
    val requestUser = authentication.principal as SecurityUser
    val user = findUserByUsername.execute(requestUser.username)

    val result = createDream.execute(dreamRequest, user.id)
      .fold(
        {
          ResponseEntity(when (it) {
            is DreamCreationErrors.TitleTooLong -> "Title is too long"
            is DreamCreationErrors.DescriptionTooLong -> "Description is too long"
            is DreamCreationErrors.TitleMisssing -> "Title is missing"
            is DreamCreationErrors.DescriptionMissing -> "Description is missing"
            is DreamCreationErrors.InvalidDate -> "Invalid dreamt date"
          }, HttpStatus.BAD_REQUEST)
        },
        { ResponseEntity(it, HttpStatus.CREATED) }
      )

    return ResponseEntity(result, HttpStatus.CREATED)
  }

}
