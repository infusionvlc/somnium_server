package com.infusionvlc.somniumserver.users.persistence

import com.infusionvlc.somniumserver.dreams.persistence.DreamEntity
import com.infusionvlc.somniumserver.users.models.User
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
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
  val dreams: List<DreamEntity> = emptyList(),

  @ManyToMany(
    fetch = FetchType.LAZY,
    cascade = [CascadeType.ALL]
  )
  @JoinTable(
    name = "users_following",
    joinColumns = [JoinColumn(
      name = "id"
    )],
    inverseJoinColumns = [JoinColumn(
      name = "id"
    )]
  )
  val following: List<UserEntity> = emptyList(),

  @ManyToMany(
    fetch = FetchType.LAZY,
    cascade = [CascadeType.ALL]
  )
  @JoinTable(
    name = "users_followers",
    joinColumns = [JoinColumn(
      name = "id"
    )],
    inverseJoinColumns = [JoinColumn(
      name = "id"
    )]
  )
  val followers: List<UserEntity> = emptyList()
)

fun UserEntity.toDomain(): User = User(
  id = id,
  username = username,
  password = password,
  followings = this.following.map { it.id },
  followers = this.followers.map { it.id }
)
