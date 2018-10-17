package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.Option
import arrow.core.Try
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamRemovalErrors
import com.infusionvlc.somniumserver.dreams.persistence.DreamDAO
import com.infusionvlc.somniumserver.mock
import io.kotlintest.Description
import io.kotlintest.assertions.arrow.either.shouldBeLeft
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.specs.StringSpec
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`

class DeleteDreamTest : StringSpec() {
  private val mockDao = mock<DreamDAO>()
  private val deleteDream = DeleteDream(mockDao)

  override fun beforeTest(description: Description) {
    super.beforeTest(description)
    mockDaoWillSuccessAtDeletion()
  }

  init {

    "If user is not creator of dream an error should be returned" {
      mockDaoWillFindDream()

      val result = deleteDream.execute(1, 1)

      result.shouldBeLeft(DreamRemovalErrors.UserIsNotCreator)
    }

    "If dream is not found an error should be returned" {
      mockDaoWontFindDream()

      val result = deleteDream.execute(1, 1)

      result.shouldBeLeft()
      (result as Either.Left<DreamRemovalErrors>).a.shouldBeTypeOf<DreamRemovalErrors.DreamNotFound>()
    }

    "If dream is found and creator is okay then success should be returned" {
      mockDaoWillFindDream()

      val result = deleteDream.execute(1, 0)

      result.shouldBeRight()
    }
  }

  private fun mockDaoWontFindDream() {
    `when`(mockDao.findById(ArgumentMatchers.anyLong())).thenReturn(Option.empty())
  }

  private fun mockDaoWillFindDream() {
    `when`(mockDao.findById(ArgumentMatchers.anyLong())).thenReturn(Option.just(Dream()))
  }

  private fun mockDaoWillSuccessAtDeletion() {
    `when`(mockDao.deleteDream(ArgumentMatchers.anyLong())).thenReturn(Try.just(Unit))
  }
}
