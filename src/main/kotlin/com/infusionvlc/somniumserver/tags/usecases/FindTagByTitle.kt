package com.infusionvlc.somniumserver.tags.usecases

import arrow.core.Option
import arrow.core.toOption
import com.infusionvlc.somniumserver.tags.models.Tag
import com.infusionvlc.somniumserver.tags.persistence.TagDAO
import com.infusionvlc.somniumserver.tags.persistence.TagLocalDatasource
import com.infusionvlc.somniumserver.tags.persistence.toDomain
import org.springframework.stereotype.Component

@Component
class FindTagByTitle(private val dao: TagDAO) {
  fun execute(title: String): Option<Tag> = dao.findByTitle(title)
}
