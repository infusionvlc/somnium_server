package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.infusionvlc.somniumserver.base.toOption
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamCreationErrors
import com.infusionvlc.somniumserver.dreams.models.DreamEditionErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRequest
import com.infusionvlc.somniumserver.dreams.persistence.DreamRepository
import com.infusionvlc.somniumserver.dreams.persistence.toDomain
import com.infusionvlc.somniumserver.dreams.persistence.toEntity
import com.infusionvlc.somniumserver.users.usecases.FindUserById
import org.springframework.stereotype.Component

@Component
class EditDream(
  private val dao: DreamRepository,
  private val findUserById: FindUserById
) {
  fun execute(
    dreamId: Long,
    dreamRequest: DreamRequest,
    userId: Long,
    currentTime: Long = System.currentTimeMillis()
  ): Either<DreamEditionErrors, Dream> = dao.findById(dreamId)
    .toOption()
    .map { it.toDomain() }
    .toEither { DreamEditionErrors.DreamNotFound(dreamId) }
    .flatMap {
      if (isUserCreatorOfDream(userId, it))
        it.right()
      else
        DreamEditionErrors.UserIsNotCreator.left()
    }
    .map {
      it.copy(
        title = dreamRequest.title,
        description = dreamRequest.description,
        dreamtDate = dreamRequest.dreamtDate,
        updateDate = currentTime
      )
    }
    .flatMap { validateDream(it, currentTime) }
    .flatMap { dream ->
      findUserById.execute(userId)
        .toEither { DreamCreationErrors.CreatorNotFound(userId) }
        .map { dao.save(dream.toEntity(it)) }
    }
    .map { it.toDomain() }
}
