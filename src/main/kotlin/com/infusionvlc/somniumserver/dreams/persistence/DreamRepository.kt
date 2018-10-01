package com.infusionvlc.somniumserver.dreams.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository

interface DreamRepository : PagingAndSortingRepository<DreamEntity, Long> {

  fun findByTitleLike(title: String, pageable: Pageable): Page<DreamEntity>
}
