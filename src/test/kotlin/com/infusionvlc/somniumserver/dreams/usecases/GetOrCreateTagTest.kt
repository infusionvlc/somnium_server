package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Option
import arrow.core.Try
import com.infusionvlc.somniumserver.AnyMocker
import com.infusionvlc.somniumserver.dreams.models.TagCreationErrors
import com.infusionvlc.somniumserver.mock
import com.infusionvlc.somniumserver.tags.models.Tag
import com.infusionvlc.somniumserver.tags.persistence.TagDAO
import com.infusionvlc.somniumserver.tags.usecases.FindTagByTitle
import com.infusionvlc.somniumserver.tags.usecases.GetOrCreateTag
import io.kotlintest.assertions.arrow.either.shouldBeLeft
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.specs.StringSpec
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString

class GetOrCreateTagTest : StringSpec(), AnyMocker {

  private val mockDao = mock<TagDAO>()
  private val mockFindTagByTitle = mock<FindTagByTitle>()
  private val getOrCreateTag = GetOrCreateTag(mockDao, mockFindTagByTitle)

  private fun findTagMockWillReturnTag() {
    `when`(mockFindTagByTitle.execute(anyString())).thenReturn(Option.just(Tag(0, "Title", 123, 123)))
  }

  private fun findTagMockWillReturnEmpty() {
    `when`(mockFindTagByTitle.execute(anyString())).thenReturn(Option.empty())
  }

  init {

    "If tag title is empty an error should be returned" {
      findTagMockWillReturnEmpty()

      getOrCreateTag.execute("")
        .shouldBeLeft(TagCreationErrors.TitleMissing)
    }

    "If tag title is longer than 20 characters an error should be returned" {
      findTagMockWillReturnEmpty()

      getOrCreateTag.execute("VeryVeryVeryLongTitle")
        .shouldBeLeft(TagCreationErrors.TitleTooLong)
    }

    "If the tag didn't exist and title is valid creates a new tag and return it" {
      `when`(mockDao.save(any())).thenReturn(Try.just(Tag(0, "Title", 123, 123)))
      findTagMockWillReturnEmpty()

      getOrCreateTag.execute("Title").shouldBeRight()
    }

    "If the tag exist should return the tag" {
      findTagMockWillReturnTag()

      getOrCreateTag.execute("Title").shouldBeRight()
    }
  }
}
