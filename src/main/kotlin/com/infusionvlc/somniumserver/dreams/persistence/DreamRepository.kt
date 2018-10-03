package com.infusionvlc.somniumserver.dreams.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

interface DreamRepository : PagingAndSortingRepository<DreamEntity, Long> {
  @Query("Select d from Dreams d where d.isPublic = true or (d.isPublic = false and d.user_id = :userId)")
  fun findAllVisibleByUser(userId: Long, pageable: Pageable): Page<DreamEntity>

  fun findByTitleContainingIgnoreCase(title: String, pageable: Pageable): Page<DreamEntity>
}
