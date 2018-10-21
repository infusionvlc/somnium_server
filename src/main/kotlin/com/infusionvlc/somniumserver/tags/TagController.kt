package com.infusionvlc.somniumserver.tags

import com.infusionvlc.somniumserver.tags.models.Tag
import com.infusionvlc.somniumserver.tags.usecases.SearchTag
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tags/v1")
@Api(value = "Tags", description = "Endpoints for Tags resources")
class TagController(
  private val searchTag: SearchTag
) {

  @GetMapping("/search")
  @ApiOperation(value = "Search tag by title")
  @ApiResponses(
    ApiResponse(code = 200, response = Tag::class, responseContainer = "List", message = "Tags result")
  )
  fun searchTag(
    @RequestParam("title") title: String,
    @RequestParam("page") page: Int,
    @RequestParam("page_size") pageSize: Int
  ): ResponseEntity<List<Tag>> =
    ResponseEntity.ok(searchTag.execute(title, page, pageSize))
}
