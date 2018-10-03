package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.infusionvlc.somniumserver.base.toOption
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamDetailErrors
import com.infusionvlc.somniumserver.dreams.persistence.DreamRepository
import com.infusionvlc.somniumserver.dreams.persistence.toDomain
import org.springframework.stereotype.Component

@Component
class GetDreamById(private val dao: DreamRepository) {
  fun execute(dreamId: Long, userId: Long) : Either<DreamDetailErrors, Dream> = dao.findById(dreamId).toOption()
    .map { it.toDomain() }
    .toEither { DreamDetailErrors.DreamNotFound(dreamId) }
    .flatMap {
      if (!it.isPublic && !isUserCreatorOfDream(userId, it)) DreamDetailErrors.DreamIsNotPublic.left()
      else it.right()
    }
}
