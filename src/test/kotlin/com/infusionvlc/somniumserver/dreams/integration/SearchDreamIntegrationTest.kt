package com.infusionvlc.somniumserver.dreams.integration

import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.persistence.DreamEntity
import com.infusionvlc.somniumserver.dreams.persistence.DreamLocalDatasource
import com.infusionvlc.somniumserver.dreams.persistence.toDomain
import com.infusionvlc.somniumserver.getForEntityAuthorized
import com.infusionvlc.somniumserver.users.persistence.UserEntity
import com.infusionvlc.somniumserver.users.persistence.UserRepository
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestContextManager
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchDreamIntegrationTest : WordSpec() {

  private lateinit var FREDDY_DREAM: Dream
  private lateinit var NOT_PASSING_TESTS_DREAM: Dream
  private lateinit var NUN_DREAM: Dream

  private fun createDream(title: String, user: UserEntity): DreamEntity =
    DreamEntity(0, title, "He was in my dreams", 3, 23, 3, true, user)

  private val restTemplate = TestRestTemplate().restTemplate

  @LocalServerPort
  var port: Int = 0

  @Autowired
  lateinit var dreamDao: DreamLocalDatasource
  @Autowired
  lateinit var userDao: UserRepository

  override fun beforeSpec(description: Description, spec: Spec) {
    TestContextManager(this.javaClass).prepareTestInstance(this)
  }

  init {
    "Search dream" should {
      givenDreams()

      "Get Freddy Krueger dream if contains \"fre\" words" {
        val result = restTemplate
          .getForEntityAuthorized<Array<Dream>>("http://localhost:$port/dreams/v1/search?title=fre&page=0&page_size=20")

        result.statusCode shouldBe HttpStatus.OK
        result.body!!.toList() shouldContain FREDDY_DREAM
      }

      "Get all dreams which contain T" {
        val result = restTemplate
          .getForEntityAuthorized<Array<Dream>>("http://localhost:$port/dreams/v1/search?title=t&page=0&page_size=20")

        result.statusCode shouldBe HttpStatus.OK
        result.body!!.toList() shouldContainAll listOf(NOT_PASSING_TESTS_DREAM, NUN_DREAM)
      }

      "Get Not passing test dream if search by the complete title" {
        val result = restTemplate
          .getForEntityAuthorized<Array<Dream>>("http://localhost:$port/dreams/v1/search?title=My tests don't pass&page=0&page_size=20")

        result.statusCode shouldBe HttpStatus.OK
        result.body!!.toList() shouldContain NOT_PASSING_TESTS_DREAM
      }

      "Case insensitive" {
        val result = restTemplate
          .getForEntityAuthorized<Array<Dream>>("http://localhost:$port/dreams/v1/search?title=FREDDY krueger&page=0&page_size=20")

        result.statusCode shouldBe HttpStatus.OK
        result.body!!.toList() shouldContain FREDDY_DREAM
      }
    }
  }

  fun givenDreams() {
    val tonilopezmr = UserEntity(0, "tonilopezmr", "freddy")
    val insertedToni = userDao.save(tonilopezmr)
    FREDDY_DREAM = dreamDao.save(createDream("Freddy Krueger", insertedToni)).toDomain()
    NOT_PASSING_TESTS_DREAM = dreamDao.save(createDream("My tests don't pass", insertedToni)).toDomain()
    NUN_DREAM = dreamDao.save(createDream("The nun is following me", insertedToni)).toDomain()
  }
}
