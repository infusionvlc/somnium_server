package com.infusionvlc.somniumserver.tags.persistence

import org.springframework.data.domain.Page
import org.springframework.data.repository.CrudRepository
import org.springframework.data.domain.Pageable

interface TagLocalDatasource : CrudRepository<TagEntity, Long> {
  fun findByTitle(title: String): TagEntity?
  fun findByTitleContainingIgnoreCase(title: String, pageable: Pageable): Page<TagEntity>
}
