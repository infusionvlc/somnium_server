package com.infusionvlc.somniumserver.dreams.persistence

import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.users.persistence.UserEntity
import com.infusionvlc.somniumserver.tags.persistence.TagEntity
import com.infusionvlc.somniumserver.tags.persistence.toDomain
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.ManyToMany

@Entity
@Table(name = "Dreams")
data class DreamEntity(
  @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
  val title: String = "",
  val description: String = "",
  val creationDate: Long = 0,
  val updateDate: Long = 0,
  val dreamtDate: Long = 0,
  val public: Boolean = true,

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  val user: UserEntity = UserEntity(),

  @ManyToMany(mappedBy = "dreams", fetch = FetchType.EAGER)
  var tags: List<TagEntity> = mutableListOf()
)

fun DreamEntity.toDomain(): Dream = Dream(
  id = this.id,
  title = this.title,
  description = this.description,
  userId = this.user.id,
  tags = this.tags.map { it.toDomain() }.toMutableList(),
  creationDate = this.creationDate,
  updateDate = this.updateDate,
  dreamtDate = this.dreamtDate,
  public = this.public
)
