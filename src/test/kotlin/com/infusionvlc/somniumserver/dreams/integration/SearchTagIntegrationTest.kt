package com.infusionvlc.somniumserver.dreams.integration

import com.infusionvlc.somniumserver.getForEntityAuthorized
import com.infusionvlc.somniumserver.tags.models.Tag
import com.infusionvlc.somniumserver.tags.persistence.TagEntity
import com.infusionvlc.somniumserver.tags.persistence.TagLocalDatasource
import com.infusionvlc.somniumserver.tags.persistence.toDomain
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
class SearchTagIntegrationTest : WordSpec() {

  private lateinit var NIGHTMARE_TAG: Tag
  private lateinit var DEJAVU_TAG: Tag
  private lateinit var LUCID_TAG: Tag

  private val restTemplate = TestRestTemplate().restTemplate

  @LocalServerPort
  var port: Int = 0

  @Autowired
  lateinit var localDatasource: TagLocalDatasource
  @Autowired
  lateinit var userDao: UserRepository

  override fun beforeSpec(description: Description, spec: Spec) {
    TestContextManager(this.javaClass).prepareTestInstance(this)
  }

  init {
    "Search tags" should {
      givenTags()

      "Get Nightmare tag ig contains 'night' words" {
        val result = restTemplate.
          getForEntityAuthorized<Array<Tag>>("http://localhost:$port/tags/v1/search?title=night&page=0&page_size=20")

        result.statusCode shouldBe HttpStatus.OK
        result.body!!.toList() shouldContain NIGHTMARE_TAG
      }

      "Get all tags that cointains the letter 'a' in the title" {
        val result = restTemplate.
          getForEntityAuthorized<Array<Tag>>("http://localhost:$port/tags/v1/search?title=a&page=0&page_size=20")

        result.statusCode shouldBe HttpStatus.OK
        result.body!!.toList() shouldContainAll listOf(NIGHTMARE_TAG, DEJAVU_TAG)
      }

      "Insensitive case search" {
        val result = restTemplate.
          getForEntityAuthorized<Array<Tag>>("http://localhost:$port/tags/v1/search?title=lUcId&page=0&page_size=20")

        result.statusCode shouldBe HttpStatus.OK
        result.body!!.toList() shouldContain LUCID_TAG
      }
    }
  }

  fun givenTags() {
    NIGHTMARE_TAG = localDatasource.save(TagEntity(1, "Nightmare", 1234567890, 1234567890, emptyList())).toDomain()
    DEJAVU_TAG = localDatasource.save(TagEntity(2, "DejaVu", 1234567890, 1234567890, emptyList())).toDomain()
    LUCID_TAG = localDatasource.save(TagEntity(3, "Lucid", 1234567890, 1234567890, emptyList())).toDomain()
  }
}
