package com.infusionvlc.somniumserver.tags.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.infusionvlc.somniumserver.tags.models.Tag
import com.infusionvlc.somniumserver.tags.models.TagCreationErrors
import com.infusionvlc.somniumserver.tags.persistence.TagRepository
import com.infusionvlc.somniumserver.tags.persistence.toDomain
import com.infusionvlc.somniumserver.tags.persistence.toEntity
import org.springframework.stereotype.Component

@Component
class GetOrCreateTag(
  private val dao: TagRepository,
  private val findTagByTitle: FindTagByTitle
) {
  fun execute(title: String): Either<TagCreationErrors, Tag> =
    findTagByTitle.execute(title)
      .toEither {
        validate(title)
          .flatMap { tag ->
            dao.save(tag.toEntity()).toDomain().right()
          }
      }


  private fun validate(title: String): Either<TagCreationErrors, Tag> = when {
    title.length > 20 -> TagCreationErrors.TitleTooLong.left()
    title.isBlank() -> TagCreationErrors.TitleMissing.left()
    else -> Tag(0, title, System.currentTimeMillis(), System.currentTimeMillis()).right()
  }
}
