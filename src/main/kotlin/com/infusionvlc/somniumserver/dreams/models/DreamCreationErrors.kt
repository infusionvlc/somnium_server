package com.infusionvlc.somniumserver.dreams.models

sealed class DreamCreationErrors {
  object TitleTooLong : DreamCreationErrors()
  object DescriptionTooLong : DreamCreationErrors()
  object TitleMissing : DreamCreationErrors()
  object DescriptionMissing : DreamCreationErrors()
  object InvalidDate : DreamCreationErrors()
  class CreatorNotFound(val userId: Long) : DreamCreationErrors()
}
