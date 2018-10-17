package com.infusionvlc.somniumserver.dreams.usecases

import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.persistence.DreamDAO
import org.springframework.stereotype.Component

@Component
class SearchDream(
  private val dao: DreamDAO
) {
  fun execute(title: String, page: Int, pageSize: Int): List<Dream> =
    dao.searchDream(title, page, pageSize)
}
