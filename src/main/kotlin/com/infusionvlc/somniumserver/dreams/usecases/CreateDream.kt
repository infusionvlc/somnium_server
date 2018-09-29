package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamCreationErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRequest
import com.infusionvlc.somniumserver.dreams.models.toDomain
import com.infusionvlc.somniumserver.dreams.persistence.DreamRepository
import com.infusionvlc.somniumserver.dreams.persistence.toDomain
import com.infusionvlc.somniumserver.dreams.persistence.toEntity
import org.springframework.stereotype.Component

@Component
class CreateDream(
  private val dao: DreamRepository
) {
  fun execute(dreamRequest: DreamRequest, userId: Long): Either<DreamCreationErrors, Dream> {
    val dream = dreamRequest.toDomain(userId)
    val validationResult = validate(dream)

    return validationResult
      .map { dao.save(it.toEntity()) }
      .map { it.toDomain() }
  }

  private fun validate(dream: Dream): Either<DreamCreationErrors, Dream> = when {
    dream.title.length >= 40 -> DreamCreationErrors.TitleTooLong.left()
    dream.title.isBlank() -> DreamCreationErrors.TitleMisssing.left()
    dream.description.length >= 200 -> DreamCreationErrors.DescriptionTooLong.left()
    dream.description.isBlank() -> DreamCreationErrors.DescriptionMissing.left()
    dream.dreamtDate >= System.currentTimeMillis() -> DreamCreationErrors.InvalidDate.left()
    else -> dream.right()
  }
}
