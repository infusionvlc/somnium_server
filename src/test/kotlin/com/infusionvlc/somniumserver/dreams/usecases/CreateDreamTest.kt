package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.Option
import arrow.core.Try
import com.infusionvlc.somniumserver.AnyMocker
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamCreationErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRequest
import com.infusionvlc.somniumserver.dreams.models.toDomain
import com.infusionvlc.somniumserver.dreams.persistence.DreamDAO
import com.infusionvlc.somniumserver.mock
import com.infusionvlc.somniumserver.users.models.User
import com.infusionvlc.somniumserver.users.usecases.FindUserById
import io.kotlintest.assertions.arrow.either.shouldBeLeft
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.specs.StringSpec
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.`when`

class CreateDreamTest : StringSpec(), AnyMocker {

  private val mockDao = mock<DreamDAO>()
  private val mockFindUser = mock<FindUserById>()
  private val createDream = CreateDream(mockDao, mockFindUser)

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

      createDream.execute(emptyTitleDreamRequest, 2000)
        .shouldBeLeft(DreamCreationErrors.TitleMissing)
    }

    "If dream description is empty an error should be returned" {
      findUserMockWillReturnUser()

      val emptyDescriptionDreamRequest = DreamRequest(title = "Test", dreamtDate = 1000)
        .toDomain(100)

      createDream.execute(emptyDescriptionDreamRequest, 0, 2000)
        .shouldBeLeft(DreamCreationErrors.DescriptionMissing)
    }

    "If dream title is longer than 40 characters an error should be returned" {
      findUserMockWillReturnUser()

      val longTitle = "x".repeat(41)
      val longTitleDreamRequest = DreamRequest(longTitle, "Test", 1000)
        .toDomain(100)

      createDream.execute(longTitleDreamRequest, 0, 2000)
        .shouldBeLeft(DreamCreationErrors.TitleTooLong)
    }

    "If dream description is longer than 200 characters an error should be returned" {
      findUserMockWillReturnUser()

      val longDescription = "x".repeat(201)
      val longDescriptionDreamRequest = DreamRequest("Test", longDescription, 1000)
        .toDomain(100)

      createDream.execute(longDescriptionDreamRequest, 0, 2000)
        .shouldBeLeft(DreamCreationErrors.DescriptionTooLong)
    }

    "If dream dreamt date is placed in the future an error should be returned" {
      findUserMockWillReturnUser()

      val futureDateDreamRequest = DreamRequest("Test", "Description", 2000)
        .toDomain(100)

      createDream.execute(futureDateDreamRequest, 0, 1000)
        .shouldBeLeft(DreamCreationErrors.InvalidDate)
    }

    "If dream creator is not found an error should be returned" {
      findUserMockWillReturnEmpty()

      val dreamRequest = DreamRequest("Test", "Description", 1000)
        .toDomain(100)

      val result = createDream.execute(dreamRequest, 0, 2000)
      result.shouldBeLeft()
      (result as Either.Left<DreamCreationErrors>).a.shouldBeTypeOf<DreamCreationErrors.CreatorNotFound>()
    }

    "If everything goes okay a dream should be returned" {
      `when`(mockDao.saveDream(any(), any())).thenReturn(Try.just(Dream()))
      findUserMockWillReturnUser()

      val dreamRequest = DreamRequest("Test", "Description", 1000)
        .toDomain(100)

      createDream.execute(dreamRequest, 0, 2000)
        .shouldBeRight()
    }
  }
}
