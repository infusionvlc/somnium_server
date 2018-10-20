package com.infusionvlc.somniumserver.dreams.persistence

import arrow.core.Option
import arrow.core.Try
import com.infusionvlc.somniumserver.base.toOption
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.users.models.User
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class DreamDAO(private val localDatasource: DreamLocalDatasource) {

  fun findAllVisibleByUser(userId: Long, page: Int, pageSize: Int): List<Dream> = localDatasource
    .findAllVisibleByUser(userId, PageRequest.of(page, pageSize))
    .toList()
    .map(DreamEntity::toDomain)

  fun findById(dreamId: Long): Option<Dream> = localDatasource
    .findById(dreamId)
    .toOption()
    .map(DreamEntity::toDomain)

  fun searchDream(title: String, page: Int, pageSize: Int): List<Dream> = localDatasource
    .findByTitleContainingIgnoreCase(title, PageRequest.of(page, pageSize))
    .toList()
    .map(DreamEntity::toDomain)

  fun saveDream(dream: Dream, user: User): Try<Dream> = Try {
    localDatasource
      .save(dream.toEntity(user))
      .toDomain()
  }

  fun deleteDream(dreamId: Long): Try<Unit> = Try {
    localDatasource.deleteById(dreamId)
  }
}
