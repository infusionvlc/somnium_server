package com.infusionvlc.somniumserver.tags.usecases

import com.infusionvlc.somniumserver.tags.models.Tag
import com.infusionvlc.somniumserver.tags.persistence.TagEntity
import com.infusionvlc.somniumserver.tags.persistence.TagRepository
import com.infusionvlc.somniumserver.tags.persistence.toDomain
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class SearchTag(
  private val dao: TagRepository
) {
  fun execute(title: String, page: Int, pageSize: Int): List<Tag> =
    dao.findByTitleContainingIgnoreCase(title, PageRequest.of(page, pageSize))
      .toList()
      .map(TagEntity::toDomain)
}
