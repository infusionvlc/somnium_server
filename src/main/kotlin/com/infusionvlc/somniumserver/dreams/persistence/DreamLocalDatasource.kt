package com.infusionvlc.somniumserver.dreams.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

interface DreamLocalDatasource : PagingAndSortingRepository<DreamEntity, Long> {
  @Query("Select d from DreamEntity d where d.public = true or (d.public = false and d.user.id = :userId)")
  fun findAllVisibleByUser(userId: Long, pageable: Pageable): Page<DreamEntity>

  fun findByTitleContainingIgnoreCase(title: String, pageable: Pageable): Page<DreamEntity>
}
