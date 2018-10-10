package com.infusionvlc.somniumserver.tags.persistence

import org.springframework.data.repository.CrudRepository

interface TagRepository : CrudRepository<TagEntity, Long> {
  fun findByTitle(title: String): TagEntity?
}
