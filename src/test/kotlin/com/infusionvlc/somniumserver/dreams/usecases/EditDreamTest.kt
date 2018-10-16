package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.Option
import com.infusionvlc.somniumserver.dreams.models.DreamEditionErrors
import com.infusionvlc.somniumserver.dreams.models.DreamRequest
import com.infusionvlc.somniumserver.dreams.persistence.DreamEntity
import com.infusionvlc.somniumserver.dreams.persistence.DreamRepository
import com.infusionvlc.somniumserver.mock
import com.infusionvlc.somniumserver.tags.models.Tag
import com.infusionvlc.somniumserver.tags.persistence.TagEntity
import com.infusionvlc.somniumserver.tags.persistence.TagRepository
import com.infusionvlc.somniumserver.tags.usecases.FindTagByTitle
import com.infusionvlc.somniumserver.tags.usecases.GetOrCreateTag
import com.infusionvlc.somniumserver.users.models.User
import com.infusionvlc.somniumserver.users.persistence.toEntity
import com.infusionvlc.somniumserver.users.usecases.FindUserById
import io.kotlintest.assertions.arrow.either.shouldBeLeft
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.Optional

class EditDreamTest : StringSpec() {

  private val mockDao = mock<DreamRepository>()
  private val mockFindUser = mock<FindUserById>()
  private val mockGetOrCreateTag = mock<GetOrCreateTag>()
  private val editDream = EditDream(mockDao, mockFindUser, mockGetOrCreateTag)

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
    `when`(mockDao.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(DreamEntity()))
  }

  private fun mockWontFindDream() {
    `when`(mockDao.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty())
  }
}
