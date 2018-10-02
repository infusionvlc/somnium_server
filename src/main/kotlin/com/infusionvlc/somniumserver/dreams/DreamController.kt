package com.infusionvlc.somniumserver.dreams

import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamCreationErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRequest
import com.infusionvlc.somniumserver.dreams.usecases.CreateDream
import com.infusionvlc.somniumserver.dreams.usecases.GetAllDreams
import com.infusionvlc.somniumserver.dreams.usecases.SearchDream
import com.infusionvlc.somniumserver.users.security.models.SecurityUser
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

@RestController
@RequestMapping("/dreams/v1")
@Api(value = "Dreams", description = "Endpoints for Dreams resources")
class DreamController(
  private val getAllDreams: GetAllDreams,
  private val createDream: CreateDream,
  private val searchDream: SearchDream
) {

  @GetMapping("/")
  @ApiOperation(value = "Get a feed of recent Dreams")
  @ApiResponses(
    ApiResponse(code = 200, response = Dream::class, responseContainer = "List", message = "Feed of dreams")
  )
  fun getAll(
    @RequestParam("page") page: Int,
    @RequestParam("page_size") pageSize: Int
  ): ResponseEntity<List<Dream>> =
    ResponseEntity.ok(getAllDreams.execute(page, pageSize))

  @PostMapping("/")
  @ApiOperation(value = "Create a new Dream")
  @ApiResponses(
    ApiResponse(code = 201, response = Dream::class, message = ""),
    ApiResponse(code = 400, message = "Validation error message")
  )
  fun createDream(
    @RequestBody dreamRequest: DreamRequest,
    @ApiIgnore authentication: Authentication
  ): ResponseEntity<*> {
    val requestUser = authentication.principal as SecurityUser
    return createDream.execute(dreamRequest, requestUser.id)
      .fold(
        {
          ResponseEntity(when (it) {
            is DreamCreationErrors.TitleTooLong -> "Title is too long"
            is DreamCreationErrors.DescriptionTooLong -> "Description is too long"
            is DreamCreationErrors.TitleMissing -> "Title is missing"
            is DreamCreationErrors.DescriptionMissing -> "Description is missing"
            is DreamCreationErrors.InvalidDate -> "Invalid dreamt date"
            is DreamCreationErrors.CreatorNotFound -> "User with id ${it.userId} was not found"
          }, HttpStatus.BAD_REQUEST)
        },
        { ResponseEntity(it, HttpStatus.CREATED) }
      )
  }

  @GetMapping("/search")
  @ApiOperation(value = "Search dream by title")
  @ApiResponses(
    ApiResponse(code = 200, response = Dream::class, responseContainer = "List", message = "Dreams result")
  )
  fun searchDream(
    @RequestParam("page") page: Int,
    @RequestParam("page_size") pageSize: Int,
    @RequestParam("title") title: String
  ): ResponseEntity<List<Dream>> =
    ResponseEntity.ok(searchDream.execute(title, page, pageSize))
}
