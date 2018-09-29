package com.infusionvlc.somniumserver.dreams

import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.usecases.GetAllDreams
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dreams/v1")
class DreamController(
  private val getAllDreams: GetAllDreams
) {

  @GetMapping("/")
  fun getAll(@RequestParam("page") page: Int,
             @RequestParam("page_size") pageSize: Int): ResponseEntity<List<Dream>> =
    ResponseEntity.ok(getAllDreams.execute(page, pageSize))

}
