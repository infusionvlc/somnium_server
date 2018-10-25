package com.infusionvlc.somniumserver.tags.usecases

import arrow.core.Option
import com.infusionvlc.somniumserver.tags.models.Tag
import com.infusionvlc.somniumserver.tags.persistence.TagDAO
import org.springframework.stereotype.Component

@Component
class FindTagByTitle(private val dao: TagDAO) {
  fun execute(title: String): Option<Tag> = dao.findByTitle(title)
}
