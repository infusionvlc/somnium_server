package com.infusionvlc.somniumserver.dreams.usecases

import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.persistence.DreamDAO
import org.springframework.stereotype.Component

@Component
class GetAllDreams(
  private val dao: DreamDAO
) {
  fun execute(userId: Long, page: Int, pageSize: Int): List<Dream> =
    dao.findAllVisibleByUser(userId, page, pageSize)
}
