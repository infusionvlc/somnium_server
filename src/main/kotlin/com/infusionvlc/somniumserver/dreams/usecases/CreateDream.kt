package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.flatMap
import com.infusionvlc.somniumserver.base.toEither
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamCreationErrors
import com.infusionvlc.somniumserver.dreams.persistence.DreamDAO
import com.infusionvlc.somniumserver.users.usecases.FindUserById
import org.springframework.stereotype.Component

@Component
class CreateDream(
  private val dao: DreamDAO,
  private val findUserById: FindUserById
) {
  fun execute(
    dream: Dream,
    userId: Long,
    currentTime: Long = System.currentTimeMillis()
  ): Either<DreamCreationErrors, Dream> =
    validateDream(dream, currentTime)
      .flatMap { dream ->
        findUserById.execute(userId)
          .toEither { DreamCreationErrors.CreatorNotFound(userId) }
          .flatMap { user ->
            dao.saveDream(dream, user)
              .toEither { DreamCreationErrors.PersistenceError }
          }
      }
}
