package com.infusionvlc.somniumserver.dreams.persistence

import arrow.core.Option
import arrow.core.Try
import arrow.core.identity
import com.infusionvlc.somniumserver.base.toOption
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.tags.persistence.toEntity
import com.infusionvlc.somniumserver.users.models.User
import com.infusionvlc.somniumserver.users.persistence.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class DreamDAO(
  private val localDatasource: DreamLocalDatasource,
  private val userLocalDatasource: UserRepository
) {

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
    val dreamEntity = localDatasource.findById(dream.id)
      .toOption()
      .map {
        it.copy(
          title = dream.title,
          description = dream.description,
          creationDate = dream.creationDate,
          updateDate = dream.updateDate,
          dreamtDate = dream.dreamtDate,
          public = dream.public,
          tags = dream.tags.map { it.toEntity() },
          user = userLocalDatasource.findById(dream.userId).toOption()
            .fold({ throw Exception("Persistence error") }, ::identity)
        )
      }
      .fold({ throw Exception("Persistence error") }, ::identity)

    localDatasource
      .save(dreamEntity)
      .toDomain()
  }

  fun deleteDream(dreamId: Long): Try<Unit> = Try {
    localDatasource.deleteById(dreamId)
  }
}
