package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.ForEither
import arrow.core.applicative
import arrow.core.fix
import arrow.core.flatMap
import arrow.core.monad
import arrow.data.ListK
import arrow.data.k
import arrow.data.sequence
import arrow.typeclasses.binding
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamCreationErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRequest
import com.infusionvlc.somniumserver.dreams.models.toDomain
import com.infusionvlc.somniumserver.dreams.persistence.DreamRepository
import com.infusionvlc.somniumserver.dreams.persistence.toDomain
import com.infusionvlc.somniumserver.dreams.persistence.toEntity
import com.infusionvlc.somniumserver.tags.models.Tag
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
          .flatMap { user ->
            dreamRequest.tags
              .map(createTag::execute).k()
              .sequence(Either.applicative()).fix()
              .map { tags: ListK<Tag> ->
                val dreamWithTags = dream.copy(tags = tags)
                dao.save(dreamWithTags.toEntity(user))
              }
          }
      }
      .map { it.toDomain() }

  fun alternativeVersion(
    dreamRequest: DreamRequest,
    userId: Long,
    currentTime: Long = System.currentTimeMillis()
  ): Either<DreamCreationErrors, Dream> = Either.monad<DreamCreationErrors>().binding {
    val dream = dreamRequest.toDomain(userId)
    val validatedDream = validateDream(dream, currentTime).bind()
    val user = findUserById.execute(userId).toEither { DreamCreationErrors.CreatorNotFound(userId) }.bind()
    val tags = dreamRequest.tags.map(createTag::execute).k().sequence(Either.applicative()).fix().bind()
    val dreamWithTags = validatedDream.copy(tags = tags)
    val dreamEntity = dreamWithTags.toEntity(user)
    dao.save(dreamEntity).toDomain()
  }.fix()
}
