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
class DreamDetailsIntegrationTest : WordSpec() {

  private val restTemplate = TestRestTemplate().restTemplate

  @LocalServerPort
  var port: Int = 0

  @Autowired
  lateinit var userDao: UserRepository

  @Autowired
  lateinit var dreamDao: DreamLocalDatasource

  private lateinit var TONI_PUBLIC_DREAM: Dream
  private lateinit var TONI_PRIVATE_DREAM: Dream
  private lateinit var TEST_PRIVATE_DREAM: Dream

  override fun beforeSpec(description: Description, spec: Spec) {
    TestContextManager(this.javaClass).prepareTestInstance(this)
  }

  init {
    "Dream details" should {
      givenDreams()

      "Show dream details if it exists and is public" {
        val result = restTemplate
          .getForEntityAuthorized<Dream>("http://localhost:$port/dreams/v1/${TONI_PUBLIC_DREAM.id}")

        result.statusCode shouldBe HttpStatus.OK
        result.body!! shouldBe TONI_PUBLIC_DREAM
      }

      "Show dream details if it exists is private and logged in user is its creator" {
        val result = restTemplate
          .getForEntityAuthorized<Dream>("http://localhost:$port/dreams/v1/${TEST_PRIVATE_DREAM.id}")

        result.statusCode shouldBe HttpStatus.OK
        result.body!! shouldBe TEST_PRIVATE_DREAM
      }

      "Show error if dream exists but is private and logged in user is not its creator" {
        val result = restTemplate
          .getForEntityAuthorized<String>("http://localhost:$port/dreams/v1/${TONI_PRIVATE_DREAM.id}")

        result.statusCode shouldBe HttpStatus.FORBIDDEN
      }

      "Show error if dream does not exist" {
        val result = restTemplate
          .getForEntityAuthorized<String>("http://localhost:$port/dreams/v1/${dreamDao.count() + 20}")

        result.statusCode shouldBe HttpStatus.NOT_FOUND
      }
    }
  }

  private fun givenDreams() {
    val tonilopezmr = UserEntity(0, "tonilopezmr", "freddy")
    val insertedToni = userDao.save(tonilopezmr)
    TONI_PUBLIC_DREAM = dreamDao.save(createDream(true, insertedToni)).toDomain()
    TONI_PRIVATE_DREAM = dreamDao.save(createDream(false, insertedToni)).toDomain()
    TEST_PRIVATE_DREAM = dreamDao.findById(0).get().toDomain()
  }

  private fun createDream(public: Boolean, user: UserEntity): DreamEntity = DreamEntity(
    0,
    "Test title",
    "Dream public: $public",
    1000,
    1000,
    3000,
    public,
    user
  )
}
