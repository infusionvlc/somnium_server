package com.infusionvlc.somniumserver.tags.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.infusionvlc.somniumserver.base.toEither
import com.infusionvlc.somniumserver.dreams.models.TagCreationErrors
import com.infusionvlc.somniumserver.tags.models.Tag
import com.infusionvlc.somniumserver.tags.persistence.TagDAO
import org.springframework.stereotype.Component

@Component
class GetOrCreateTag(
  private val dao: TagDAO,
  private val findTagByTitle: FindTagByTitle
) {
  fun execute(title: String): Either<TagCreationErrors, Tag> {
    val tagOption = findTagByTitle.execute(title)

    return if (tagOption.isEmpty()) {
      validate(title)
        .flatMap { dao.save(it).toEither { TagCreationErrors.PersistenceError } }
    } else {
      tagOption.get().right()
    }
  }

  private fun validate(title: String): Either<TagCreationErrors, Tag> = when {
    title.length > 20 -> TagCreationErrors.TitleTooLong.left()
    title.isBlank() -> TagCreationErrors.TitleMissing.left()
    else -> Tag(0, title, System.currentTimeMillis(), System.currentTimeMillis()).right()
  }
}
