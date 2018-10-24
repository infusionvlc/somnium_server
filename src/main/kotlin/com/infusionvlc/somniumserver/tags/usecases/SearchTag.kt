package com.infusionvlc.somniumserver.tags.usecases

import com.infusionvlc.somniumserver.tags.models.Tag
import com.infusionvlc.somniumserver.tags.persistence.TagDAO
import com.infusionvlc.somniumserver.tags.persistence.TagEntity
import com.infusionvlc.somniumserver.tags.persistence.TagLocalDatasource
import com.infusionvlc.somniumserver.tags.persistence.toDomain
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class SearchTag(
  private val dao: TagDAO
) {
  fun execute(title: String, page: Int, pageSize: Int): List<Tag> = dao.searchTag(title, page, pageSize)
}
