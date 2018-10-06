package com.infusionvlc.somniumserver

import com.infusionvlc.somniumserver.users.security.TokenHelper
import org.mockito.Mockito
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

inline fun <reified T> RestTemplate.getForEntityAuthorized(url: String): ResponseEntity<T> {
  val headers = HttpHeaders()
  headers.set("Authorization", "Bearer ${getTokenForTestUser()}")
  val entity = HttpEntity<Any>(headers)
  return this.exchange(url, org.springframework.http.HttpMethod.GET, entity, T::class.java)
}

fun getTokenForTestUser(): String =
  TokenHelper().generateToken("Test")
