package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.infusionvlc.somniumserver.base.toOption
import com.infusionvlc.somniumserver.dreams.models.DreamRemovalErrors
import com.infusionvlc.somniumserver.dreams.persistence.DreamRepository
import com.infusionvlc.somniumserver.dreams.persistence.toDomain
import org.springframework.stereotype.Component

@Component
class DeleteDream(private val dao: DreamRepository) {
  fun execute(id: Long, userId: Long): Either<DreamRemovalErrors, Unit> {
    val dream = dao.findById(id).toOption()
      .map { it.toDomain() }

    return dream
      .toEither { DreamRemovalErrors.DreamNotFound(id) }
      .flatMap {
        if (isUserCreatorOfDream(userId, it))
          it.right()
        else
          DreamRemovalErrors.UserIsNotCreator.left()
      }
      .map { dao.deleteById(it.id) }
  }
}
