package com.infusionvlc.somniumserver.dreams.persistence

import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.users.persistence.UserEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "Dreams")
data class DreamEntity(
  @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
  val title: String = "",
  val description: String = "",
  val creationDate: Long = 0,
  val updateDate: Long = 0,
  val dreamtDate: Long = 0,

  @ManyToOne val user: UserEntity = UserEntity()
)

fun DreamEntity.toDomain(): Dream = Dream(
  id = this.id,
  title = this.title,
  description = this.description,
  userId = this.user.id,
  creationDate = this.creationDate,
  updateDate = this.updateDate,
  dreamtDate = this.dreamtDate
)

fun Dream.toEntity(): DreamEntity = DreamEntity(
  id, title, description, creationDate, updateDate, dreamtDate
)
