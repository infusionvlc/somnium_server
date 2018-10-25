package com.infusionvlc.somniumserver.tags.persistence

import arrow.core.Option
import arrow.core.Try
import arrow.core.toOption
import com.infusionvlc.somniumserver.tags.models.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class TagDAO(private val localDatasource: TagLocalDatasource) {

  fun findByTitle(title: String): Option<Tag> = Option
    .fromNullable(localDatasource.findByTitle(title).toOption().orNull())
    .map(TagEntity::toDomain)

  fun searchTag(title: String, page: Int, pageSize: Int): List<Tag> =
    localDatasource.findByTitleContainingIgnoreCase(title, PageRequest.of(page, pageSize))
      .toList()
      .map(TagEntity::toDomain)

  fun save(tag: Tag): Try<Tag> = Try {
    localDatasource
      .save(tag.toEntity())
      .toDomain()
  }
}
