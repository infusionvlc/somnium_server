package com.infusionvlc.somniumserver.dreams.persistence

import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.users.models.User
import com.infusionvlc.somniumserver.users.persistence.UserEntity
import com.infusionvlc.somniumserver.users.persistence.toEntity
import javax.persistence.*

@Entity
@Table(name = "Dreams")
data class DreamEntity(
  @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
  val title: String = "",
  val description: String = "",
  val creationDate: Long = 0,
  val updateDate: Long = 0,
  val dreamtDate: Long = 0,

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  val user: UserEntity = UserEntity()
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

fun Dream.toEntity(user: User): DreamEntity = DreamEntity(
  id, title, description, creationDate, updateDate, dreamtDate, user.toEntity()
)
