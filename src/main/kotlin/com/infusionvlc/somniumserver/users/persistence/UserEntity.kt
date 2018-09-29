package com.infusionvlc.somniumserver.users.persistence

import com.infusionvlc.somniumserver.dreams.persistence.DreamEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "Users")
data class UserEntity(
  @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
  val username: String = "",
  val password: String = "",

  @OneToMany(mappedBy = "user") val dreams: List<DreamEntity> = emptyList()
)
