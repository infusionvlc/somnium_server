package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.flatMap
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamCreationErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRequest
import com.infusionvlc.somniumserver.dreams.models.toDomain
import com.infusionvlc.somniumserver.dreams.persistence.DreamRepository
import com.infusionvlc.somniumserver.dreams.persistence.toDomain
import com.infusionvlc.somniumserver.dreams.persistence.toEntity
import com.infusionvlc.somniumserver.tags.usecases.GetOrCreateTag
import com.infusionvlc.somniumserver.users.usecases.FindUserById
import org.springframework.stereotype.Component

@Component
class CreateDream(
  private val dao: DreamRepository,
  private val findUserById: FindUserById,
  private val createTag: GetOrCreateTag
) {
  fun execute(
    dreamRequest: DreamRequest,
    userId: Long,
    currentTime: Long = System.currentTimeMillis()
  ): Either<DreamCreationErrors, Dream> =
    validateDream(dreamRequest.toDomain(userId), currentTime)
      .flatMap { dream ->
        findUserById.execute(userId)
          .toEither { DreamCreationErrors.CreatorNotFound(userId) }
          .map { user ->
            dreamRequest.tags.forEach { tag ->
              createTag.execute(tag).map { dream.tags.add(it) }
            }
            dao.save(dream.toEntity(user))
          }
      }
      .map { it.toDomain() }
}
