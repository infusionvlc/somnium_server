package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamCreationErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRequest
import com.infusionvlc.somniumserver.dreams.models.toDomain
import com.infusionvlc.somniumserver.dreams.persistence.DreamRepository
import com.infusionvlc.somniumserver.dreams.persistence.toDomain
import com.infusionvlc.somniumserver.dreams.persistence.toEntity
import com.infusionvlc.somniumserver.users.usecases.FindUserById
import org.springframework.stereotype.Component

@Component
class CreateDream(
  private val dao: DreamRepository,
  private val findUserById: FindUserById
) {
  fun execute(
    dreamRequest: DreamRequest,
    userId: Long,
    currentTime: Long = System.currentTimeMillis()
  ): Either<DreamCreationErrors, Dream> =
    validate(dreamRequest.toDomain(userId), currentTime)
      .flatMap { dream ->
        findUserById.execute(userId)
          .toEither { DreamCreationErrors.CreatorNotFound(userId) }
          .map { user -> dao.save(dream.toEntity(user)) }
      }
      .map { it.toDomain() }

  private fun validate(dream: Dream, currentTime: Long): Either<DreamCreationErrors, Dream> = when {
    dream.title.length >= 40 -> DreamCreationErrors.TitleTooLong.left()
    dream.title.isBlank() -> DreamCreationErrors.TitleMissing.left()
    dream.description.length >= 200 -> DreamCreationErrors.DescriptionTooLong.left()
    dream.description.isBlank() -> DreamCreationErrors.DescriptionMissing.left()
    dream.dreamtDate > currentTime -> DreamCreationErrors.InvalidDate.left()
    else -> dream.right()
  }
}
