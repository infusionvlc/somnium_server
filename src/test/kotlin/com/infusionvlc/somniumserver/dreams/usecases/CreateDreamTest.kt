package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.Option
import arrow.core.Try
import com.infusionvlc.somniumserver.AnyMocker
import com.infusionvlc.somniumserver.dreams.fakeDream
import com.infusionvlc.somniumserver.dreams.models.DreamCreationErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRequest
import com.infusionvlc.somniumserver.dreams.models.toDomain
import com.infusionvlc.somniumserver.dreams.persistence.DreamDAO
import com.infusionvlc.somniumserver.mock
import com.infusionvlc.somniumserver.tags.usecases.GetOrCreateTag
import com.infusionvlc.somniumserver.users.models.User
import com.infusionvlc.somniumserver.users.usecases.FindUserById
import io.kotlintest.assertions.arrow.either.shouldBeLeft
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.specs.StringSpec
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.`when`

// TODO -> Test that the tags are added
class CreateDreamTest : StringSpec(), AnyMocker {

  private val mockDao = mock<DreamDAO>()
  private val mockFindUser = mock<FindUserById>()
  private val mockCreateTag = mock<GetOrCreateTag>()
  private val createDream = CreateDream(mockDao, mockFindUser, mockCreateTag)

  private fun findUserMockWillReturnUser() {
    `when`(mockFindUser.execute(anyLong())).thenReturn(Option.just(User(0, "test", "test")))
  }

  private fun findUserMockWillReturnEmpty() {
    `when`(mockFindUser.execute(anyLong())).thenReturn(Option.empty())
  }

  init {

    "If dream title is empty an error should be returned" {
      findUserMockWillReturnUser()

      val emptyTitleDreamRequest = DreamRequest(description = "Test", dreamtDate = 1000)
        .toDomain(100)

      createDream.execute(emptyTitleDreamRequest, emptyList(), 2000)
        .shouldBeLeft(DreamCreationErrors.TitleMissing)
    }

    "If dream description is empty an error should be returned" {
      findUserMockWillReturnUser()

      val emptyDescriptionDreamRequest = DreamRequest(title = "Test", dreamtDate = 1000)
        .toDomain(100)

      createDream.execute(emptyDescriptionDreamRequest, emptyList(), 0, 2000)
        .shouldBeLeft(DreamCreationErrors.DescriptionMissing)
    }

    "If dream title is longer than 40 characters an error should be returned" {
      findUserMockWillReturnUser()

      val longTitle = "x".repeat(41)
      val longTitleDreamRequest = DreamRequest(longTitle, "Test", 1000)
        .toDomain(100)

      createDream.execute(longTitleDreamRequest, emptyList(), 0, 2000)
        .shouldBeLeft(DreamCreationErrors.TitleTooLong)
    }

    "If dream description is longer than 200 characters an error should be returned" {
      findUserMockWillReturnUser()

      val longDescription = "x".repeat(201)
      val longDescriptionDreamRequest = DreamRequest("Test", longDescription, 1000)
        .toDomain(100)

      createDream.execute(longDescriptionDreamRequest, emptyList(), 0, 2000)
        .shouldBeLeft(DreamCreationErrors.DescriptionTooLong)
    }

    "If dream dreamt date is placed in the future an error should be returned" {
      findUserMockWillReturnUser()

      val futureDateDreamRequest = DreamRequest("Test", "Description", 2000)
        .toDomain(100)

      createDream.execute(futureDateDreamRequest, emptyList(), 0, 1000)
        .shouldBeLeft(DreamCreationErrors.InvalidDate)
    }

    "If dream creator is not found an error should be returned" {
      findUserMockWillReturnEmpty()

      val dreamRequest = DreamRequest("Test", "Description", 1000)
        .toDomain(100)

      val result = createDream.execute(dreamRequest, emptyList(), 0, 2000)
      result.shouldBeLeft()
      (result as Either.Left<DreamCreationErrors>).a.shouldBeTypeOf<DreamCreationErrors.CreatorNotFound>()
    }

    "If everything goes okay a dream should be returned" {
      `when`(mockDao.saveDream(any(), any())).thenReturn(Try.just(fakeDream()))
      findUserMockWillReturnUser()

      val dreamRequest = DreamRequest("Test", "Description", 1000)
        .toDomain(100)

      createDream.execute(dreamRequest, emptyList(), 0, 2000)
        .shouldBeRight()
    }
  }
}
