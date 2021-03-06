package com.infusionvlc.somniumserver.users.persistence

import com.infusionvlc.somniumserver.dreams.persistence.DreamEntity
import com.infusionvlc.somniumserver.users.models.User
import javax.persistence.CascadeType
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

  @OneToMany(
    mappedBy = "user",
    cascade = [CascadeType.ALL],
    orphanRemoval = true
  )
  val dreams: List<DreamEntity> = emptyList()
)

fun UserEntity.toDomain(): User = User(
  id = id,
  username = username,
  password = password
)

fun User.toEntity(): UserEntity = UserEntity(
  id = id,
  username = username,
  password = password
)
