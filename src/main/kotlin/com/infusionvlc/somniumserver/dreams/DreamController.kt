package com.infusionvlc.somniumserver.dreams

import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamCreationErrors
import com.infusionvlc.somniumserver.dreams.models.DreamDetailErrors
import com.infusionvlc.somniumserver.dreams.models.DreamEditionErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRemovalErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRequest
import com.infusionvlc.somniumserver.dreams.models.TagCreationErrors
import com.infusionvlc.somniumserver.dreams.models.toDomain
import com.infusionvlc.somniumserver.dreams.usecases.CreateDream
import com.infusionvlc.somniumserver.dreams.usecases.DeleteDream
import com.infusionvlc.somniumserver.dreams.usecases.EditDream
import com.infusionvlc.somniumserver.dreams.usecases.GetAllDreams
import com.infusionvlc.somniumserver.dreams.usecases.GetDreamById
import com.infusionvlc.somniumserver.dreams.usecases.SearchDream
import com.infusionvlc.somniumserver.users.security.models.SecurityUser
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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
  private val getDreamById: GetDreamById,
  private val createDream: CreateDream,
  private val editDream: EditDream,
  private val deleteDream: DeleteDream,
  private val searchDream: SearchDream
) {

  @GetMapping("/")
  @ApiOperation(value = "Get a feed of recent Dreams")
  @ApiResponses(
    ApiResponse(code = 200, response = Dream::class, responseContainer = "List", message = "Feed of dreams")
  )
  fun getAll(
    @RequestParam("page") page: Int,
    @RequestParam("page_size") pageSize: Int,
    @ApiIgnore authentication: Authentication
  ): ResponseEntity<List<Dream>> {
    val requestUser = authentication.principal as SecurityUser
    return ResponseEntity.ok(getAllDreams.execute(requestUser.id, page, pageSize))
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "Get details of a Dream")
  @ApiResponses(
    ApiResponse(code = 200, response = Dream::class, message = "Detailed Dream"),
    ApiResponse(code = 404, message = "Dream is not found"),
    ApiResponse(code = 403, message = "Dream is not public")
  )
  fun getDreamDetails(
    @PathVariable("id") dreamId: Long,
    @ApiIgnore authentication: Authentication
  ): ResponseEntity<*> {
    val requestUser = authentication.principal as SecurityUser
    return getDreamById.execute(dreamId, requestUser.id)
      .fold(
        {
          when (it) {
            is DreamDetailErrors.DreamIsNotPublic -> handleDreamIsNotPublic()
            is DreamDetailErrors.DreamNotFound -> handleDreamNotFound(it.id)
          }
        },
        { ResponseEntity.ok(it) }
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

  @PostMapping("/")
  @ApiOperation(value = "Create a new Dream")
  @ApiResponses(
    ApiResponse(code = 201, response = Dream::class, message = "Dream is created"),
    ApiResponse(code = 400, message = "Validation error message")
  )
  fun createDream(
    @RequestBody dreamRequest: DreamRequest,
    @ApiIgnore authentication: Authentication
  ): ResponseEntity<*> {
    val requestUser = authentication.principal as SecurityUser
    return createDream.execute(dreamRequest.toDomain(requestUser.id), dreamRequest.tags, requestUser.id)
      .fold(
        this::handleDreamCreationError
      ) { ResponseEntity(it, HttpStatus.CREATED) }
  }

  @PutMapping("/{id}")
  @ApiOperation(value = "Edit an existing Dream")
  @ApiResponses(
    ApiResponse(code = 200, response = Dream::class, message = "Dream is updated"),
    ApiResponse(code = 403, message = "User is not creator of Dream"),
    ApiResponse(code = 404, message = "Dream was not found"),
    ApiResponse(code = 400, message = "Validation error message")
  )
  fun editDream(
    @RequestBody dreamRequest: DreamRequest,
    @PathVariable id: Long,
    @ApiIgnore authentication: Authentication
  ): ResponseEntity<*> {
    val requestUser = authentication.principal as SecurityUser
    return editDream.execute(id, dreamRequest, requestUser.id)
      .fold(
        {
          when (it) {
            is DreamEditionErrors.UserIsNotCreator -> handleUserIsNotCreatorOfDreamError()
            is DreamEditionErrors.DreamNotFound -> handleDreamNotFound(it.id)
            is DreamCreationErrors -> handleDreamCreationError(it)
            is DreamEditionErrors.PersistenceError -> handlePersistenceError()
          }
        },
        { ResponseEntity.ok(it) }
      )
  }

  @DeleteMapping("/{id}")
  @ApiOperation(value = "Delete an existing Dream")
  @ApiResponses(
    ApiResponse(code = 200, message = "Dream removed"),
    ApiResponse(code = 403, message = "User is not creator of Dream"),
    ApiResponse(code = 404, message = "Dream was not found")
  )
  fun deleteDream(
    @PathVariable id: Long,
    @ApiIgnore authentication: Authentication
  ): ResponseEntity<*> {
    val requestUser = authentication.principal as SecurityUser
    return deleteDream.execute(id, requestUser.id)
      .fold(
        {
          when (it) {
            is DreamRemovalErrors.UserIsNotCreator -> handleUserIsNotCreatorOfDreamError()
            is DreamRemovalErrors.DreamNotFound -> handleDreamNotFound(it.id)
            is DreamRemovalErrors.PersistenceError -> handlePersistenceError()
          }
        },
        { ResponseEntity.ok().build<Unit>() }
      )
  }

  private fun handleDreamIsNotPublic(): ResponseEntity<String> =
    ResponseEntity("Dream is not public", HttpStatus.FORBIDDEN)

  private fun handleUserIsNotCreatorOfDreamError(): ResponseEntity<String> =
    ResponseEntity("User is not creator of dream", HttpStatus.FORBIDDEN)

  private fun handleDreamNotFound(dreamId: Long): ResponseEntity<String> =
    ResponseEntity("Dream with id $dreamId was not found", HttpStatus.NOT_FOUND)

  private fun handleDreamCreationError(error: DreamCreationErrors): ResponseEntity<String> =
    ResponseEntity(when (error) {
      is DreamCreationErrors.TitleTooLong -> "Title is too long"
      is DreamCreationErrors.DescriptionTooLong -> "Description is too long"
      is DreamCreationErrors.TitleMissing -> "Title is missing"
      is DreamCreationErrors.DescriptionMissing -> "Description is missing"
      is DreamCreationErrors.InvalidDate -> "Invalid dreamt date"
      is DreamCreationErrors.CreatorNotFound -> "User with id ${error.userId} was not found"
      is DreamCreationErrors.PersistenceError -> persistenceErrorString
      is TagCreationErrors.TitleMissing -> "Tag title is missing"
      is TagCreationErrors.TitleTooLong -> "Tag title cannot be longer than 20 characters"
      is TagCreationErrors.CreationError -> "Something failed while creating tag"
      is TagCreationErrors.PersistenceError -> persistenceErrorString
    }, HttpStatus.BAD_REQUEST)

  private val persistenceErrorString = "Database operation failed"

  private fun handlePersistenceError(): ResponseEntity<String> =
    ResponseEntity(persistenceErrorString, HttpStatus.INTERNAL_SERVER_ERROR)
}
