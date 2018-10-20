package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.Option
import com.infusionvlc.somniumserver.dreams.fakeDream
import com.infusionvlc.somniumserver.dreams.models.DreamEditionErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRequest
import com.infusionvlc.somniumserver.dreams.persistence.DreamDAO
import com.infusionvlc.somniumserver.mock
import com.infusionvlc.somniumserver.users.usecases.FindUserById
import io.kotlintest.assertions.arrow.either.shouldBeLeft
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.specs.StringSpec
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`

class EditDreamTest : StringSpec() {

  private val mockDao = mock<DreamDAO>()
  private val mockFindUser = mock<FindUserById>()
  private val editDream = EditDream(mockDao, mockFindUser)

  init {

    "If user is not creator of dream an error should be returned" {
      mockWillFindDream()

      val result = editDream.execute(1, DreamRequest(), 1, 1000)

      result.shouldBeLeft(DreamEditionErrors.UserIsNotCreator)
    }

    "If dream is not found an error should be returned" {
      mockWontFindDream()

      val result = editDream.execute(1, DreamRequest(), 1, 1000)

      result.shouldBeLeft()
      (result as Either.Left<DreamEditionErrors>).a.shouldBeTypeOf<DreamEditionErrors.DreamNotFound>()
    }
  }

  private fun mockWillFindDream() {
    `when`(mockDao.findById(ArgumentMatchers.anyLong())).thenReturn(Option.just(fakeDream()))
  }

  private fun mockWontFindDream() {
    `when`(mockDao.findById(ArgumentMatchers.anyLong())).thenReturn(Option.empty())
  }
}
