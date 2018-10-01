package com.infusionvlc.somniumserver.dreams

import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.persistence.DreamEntity
import com.infusionvlc.somniumserver.dreams.persistence.DreamRepository
import com.infusionvlc.somniumserver.dreams.persistence.toDomain
import com.infusionvlc.somniumserver.users.persistence.UserEntity
import com.infusionvlc.somniumserver.users.persistence.UserRepository
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.specs.WordSpec
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestContextManager
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest()
class SearchDreamTest : WordSpec() {

  companion object {
    private lateinit var FREDDY_DREAM: Dream
    private lateinit var NOT_PASSING_TESTS_DREAM: Dream
    private lateinit var NUN_DREAM: Dream

    private fun createDream(title: String, user: UserEntity): DreamEntity =
      DreamEntity(0, title, "He was in my dreams", 3, 23, 3, user)
  }

  @Autowired
  lateinit var controller: DreamController

  @Autowired
  lateinit var dreamDao: DreamRepository
  @Autowired
  lateinit var userDao: UserRepository

  override fun beforeSpec(description: Description, spec: Spec) {
    TestContextManager(this.javaClass).prepareTestInstance(this)
  }

  init {
    "search dream" should {
      givenDreams()

      "Get Freddy Krueger dream if contains \"fre\" words" {
        val searchResponse = controller.searchDream(0, 20, "fre")

        searchResponse.body!! shouldContain FREDDY_DREAM
      }

      "Get all dreams which contain T" {
        val searchResponse = controller.searchDream(0, 20, "t")

        searchResponse.body!! shouldContainAll listOf(NOT_PASSING_TESTS_DREAM, NUN_DREAM)
      }

      "Get Not passing test dream if search by the complete title" {
        val searchResponse = controller.searchDream(0, 20, "My tests don't pass")

        searchResponse.body!! shouldContain NOT_PASSING_TESTS_DREAM
      }

      "Case insensitive" {
        val searchResponse = controller.searchDream(0, 20, "FREDDY krueger")

        searchResponse.body!! shouldContain FREDDY_DREAM
      }
    }
  }

  fun givenDreams() {
    val tonilopezmr = UserEntity(1, "tonilopezmr", "freddy")
    userDao.save(tonilopezmr)
    FREDDY_DREAM = dreamDao.save(createDream("Freddy Krueger", tonilopezmr)).toDomain()
    NOT_PASSING_TESTS_DREAM = dreamDao.save(createDream("My tests don't pass", tonilopezmr)).toDomain()
    NUN_DREAM = dreamDao.save(createDream("The nun is following me", tonilopezmr)).toDomain()
  }
}
