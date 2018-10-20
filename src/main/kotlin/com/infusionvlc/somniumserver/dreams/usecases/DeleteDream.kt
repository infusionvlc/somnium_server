package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.infusionvlc.somniumserver.base.toEither
import com.infusionvlc.somniumserver.dreams.models.DreamRemovalErrors
import com.infusionvlc.somniumserver.dreams.persistence.DreamDAO
import org.springframework.stereotype.Component

@Component
class DeleteDream(private val dao: DreamDAO) {
  fun execute(id: Long, userId: Long): Either<DreamRemovalErrors, Unit> =
    dao.findById(id)
      .toEither { DreamRemovalErrors.DreamNotFound(id) }
      .flatMap {
        if (isUserCreatorOfDream(userId, it))
          it.right()
        else
          DreamRemovalErrors.UserIsNotCreator.left()
      }
      .flatMap {
        dao.deleteDream(it.id)
          .toEither { DreamRemovalErrors.PersistenceError }
      }
}
