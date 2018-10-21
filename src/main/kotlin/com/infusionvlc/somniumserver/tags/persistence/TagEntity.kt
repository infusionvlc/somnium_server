package com.infusionvlc.somniumserver.tags.persistence

import com.infusionvlc.somniumserver.dreams.persistence.DreamEntity
import com.infusionvlc.somniumserver.tags.models.Tag
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "Tags")
data class TagEntity(
  @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
  val title: String = "",
  val creationDate: Long = 0,
  val updateDate: Long = 0,

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "tag_dream",
    joinColumns = [(JoinColumn(name = "tag_id", referencedColumnName = "id"))],
    inverseJoinColumns = [(JoinColumn(name = "dream_id", referencedColumnName = "id"))])
  var dreams: List<DreamEntity> = mutableListOf()
)

fun TagEntity.toDomain(): Tag = Tag(
  id = this.id,
  title = this.title,
  creationDate = this.creationDate,
  updateDate = this.updateDate
)

fun Tag.toEntity(): TagEntity = TagEntity(id, title, creationDate, updateDate)
