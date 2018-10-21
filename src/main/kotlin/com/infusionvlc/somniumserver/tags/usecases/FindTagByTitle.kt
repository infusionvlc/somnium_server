package com.infusionvlc.somniumserver.tags.usecases

import arrow.core.Option
import arrow.core.toOption
import com.infusionvlc.somniumserver.tags.models.Tag
import com.infusionvlc.somniumserver.tags.persistence.TagRepository
import com.infusionvlc.somniumserver.tags.persistence.toDomain
import org.springframework.stereotype.Component

@Component
class FindTagByTitle(private val dao: TagRepository) {
  fun execute(title: String): Option<Tag> = Option
    .fromNullable(dao.findByTitle(title).toOption().orNull())
    .map { it.toDomain() }
}
