package com.infusionvlc.somniumserver.dreams.usecases

import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.persistence.DreamRepository
import com.infusionvlc.somniumserver.dreams.persistence.toDomain
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class GetAllDreams(
  private val dao: DreamRepository
) {
  fun execute(page: Int, pageSize: Int): List<Dream> =
    dao.findAll(PageRequest.of(page, pageSize))
      .map { it.toDomain() }
      .toList()
}
