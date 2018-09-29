package com.infusionvlc.somniumserver.dreams.models

sealed class DreamCreationErrors {
  object TitleTooLong : DreamCreationErrors()
  object DescriptionTooLong : DreamCreationErrors()
  object TitleMisssing : DreamCreationErrors()
  object DescriptionMissing : DreamCreationErrors()
  object InvalidDate : DreamCreationErrors()
}
