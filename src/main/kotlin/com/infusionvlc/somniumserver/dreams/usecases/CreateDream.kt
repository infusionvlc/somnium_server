package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.applicative
import arrow.core.fix
import arrow.core.flatMap
import arrow.core.left
import arrow.core.monad
import arrow.core.right
import arrow.data.ListK
import arrow.data.k
import arrow.data.sequence
import arrow.typeclasses.binding
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamCreationErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRequest
import com.infusionvlc.somniumserver.dreams.models.toDomain
import com.infusionvlc.somniumserver.dreams.persistence.DreamDAO
import com.infusionvlc.somniumserver.tags.models.Tag
import com.infusionvlc.somniumserver.tags.usecases.GetOrCreateTag
import com.infusionvlc.somniumserver.users.usecases.FindUserById
import org.springframework.stereotype.Component

@Component
class CreateDream(
  private val dao: DreamDAO,
  private val findUserById: FindUserById,
  private val createTag: GetOrCreateTag
) {
  fun execute(
    dream: Dream,
    tags: List<String>,
    userId: Long,
    currentTime: Long = System.currentTimeMillis()
  ): Either<DreamCreationErrors, Dream> =
    validateDream(dream, currentTime)
      .flatMap { dream ->
        findUserById.execute(userId)
          .toEither { DreamCreationErrors.CreatorNotFound(userId) }
          .flatMap { user ->
            tags
              .map(createTag::execute).k()
              .sequence(Either.applicative()).fix()
              .flatMap { tags: ListK<Tag> ->
                val dreamWithTags = dream.copy(tags = tags)
                dao.saveDream(dreamWithTags, user)
                  .fold({ DreamCreationErrors.PersistenceError.left() }, { it.right() })
              }
          }
      }

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
    dao.saveDream(dreamWithTags, user)
      .fold({ DreamCreationErrors.PersistenceError.left() }, { it.right() }).bind()
  }.fix()
}
