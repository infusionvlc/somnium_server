package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamDetailErrors
import com.infusionvlc.somniumserver.dreams.persistence.DreamDAO
import org.springframework.stereotype.Component

@Component
class GetDreamById(private val dao: DreamDAO) {
  fun execute(dreamId: Long, userId: Long): Either<DreamDetailErrors, Dream> = dao.findById(dreamId)
    .toEither { DreamDetailErrors.DreamNotFound(dreamId) }
    .flatMap {
      if (!it.public && !isUserCreatorOfDream(userId, it)) DreamDetailErrors.DreamIsNotPublic.left()
      else it.right()
    }
}
