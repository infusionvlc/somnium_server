package com.infusionvlc.somniumserver.dreams.usecases

import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.persistence.DreamRepository
import com.infusionvlc.somniumserver.dreams.persistence.toDomain
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class SearchDream(
  private val dao: DreamRepository
) {
  operator fun invoke(title: String, page: Int, pageSize: Int): List<Dream> =
    dao.findByTitleContainingIgnoreCase(title, PageRequest.of(page, pageSize))
      .map { it.toDomain() }
      .toList()
}
