package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import arrow.syntax.function.andThen
import com.infusionvlc.somniumserver.base.toOption
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamCreationErrors
import com.infusionvlc.somniumserver.dreams.models.DreamEditionErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRequest
import com.infusionvlc.somniumserver.dreams.persistence.DreamRepository
import com.infusionvlc.somniumserver.dreams.persistence.toDomain
import com.infusionvlc.somniumserver.dreams.persistence.toEntity
import com.infusionvlc.somniumserver.tags.models.Tag
import com.infusionvlc.somniumserver.tags.usecases.GetOrCreateTag
import com.infusionvlc.somniumserver.users.usecases.FindUserById
import org.springframework.stereotype.Component

@Component
class EditDream(
  private val dao: DreamRepository,
  private val findUserById: FindUserById,
  private val getOrCreateTag: GetOrCreateTag
) {
  fun execute(
    dreamId: Long,
    dreamRequest: DreamRequest,
    userId: Long,
    currentTime: Long = System.currentTimeMillis()
  ): Either<DreamEditionErrors, Dream> = (
    retrieveDreamFromPersistence(dao, dreamId) andThen
      checkUserIsCreatorOfDream(userId) andThen
      updateDream(dreamRequest, currentTime) andThen
      validate(currentTime) andThen
      storeDream(userId)
    )()

  private fun retrieveDreamFromPersistence(dao: DreamRepository, dreamId: Long) = {
    dao.findById(dreamId)
      .toOption()
      .map { it.toDomain() }
      .toEither { DreamEditionErrors.DreamNotFound(dreamId) }
  }

  private fun checkUserIsCreatorOfDream(userId: Long) = { either: Either<DreamEditionErrors, Dream> ->
    either.flatMap {
      if (isUserCreatorOfDream(userId, it))
        it.right()
      else
        DreamEditionErrors.UserIsNotCreator.left()
    }
  }

  private fun updateDream(dreamRequest: DreamRequest, currentTime: Long) =
    { either: Either<DreamEditionErrors, Dream> ->
      either.map {
        it.copy(
          title = dreamRequest.title,
          description = dreamRequest.description,
          tags = getOrCreateTags(dreamRequest.tags),
          dreamtDate = dreamRequest.dreamtDate,
          updateDate = currentTime
        )
      }
    }

  private fun validate(currentTime: Long) = { either: Either<DreamEditionErrors, Dream> ->
    either.flatMap { validateDream(it, currentTime) }
  }

  private fun getOrCreateTags(tagsList: List<String>): MutableList<Tag> {
    val tags = mutableListOf<Tag>()
    tagsList.forEach { tag ->
      getOrCreateTag.execute(tag).map { tags.add(it) }
    }
    return tags
  }


  private fun storeDream(userId: Long) = { either: Either<DreamEditionErrors, Dream> ->
    either.flatMap { dream ->
      findUserById.execute(userId)
        .toEither { DreamCreationErrors.CreatorNotFound(userId) }
        .map { dao.save(dream.toEntity(it)) }
        .map { it.toDomain() }
    }
  }
}
