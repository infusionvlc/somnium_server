package com.infusionvlc.somniumserver.dreams.models

sealed class DreamEditionErrors {
  object UserIsNotCreator : DreamEditionErrors()
  class DreamNotFound(val id: Long) : DreamEditionErrors()
  object PersistenceError : DreamEditionErrors()
}

sealed class DreamCreationErrors : DreamEditionErrors() {
  object TitleTooLong : DreamCreationErrors()
  object DescriptionTooLong : DreamCreationErrors()
  object TitleMissing : DreamCreationErrors()
  object DescriptionMissing : DreamCreationErrors()
  object InvalidDate : DreamCreationErrors()
  class CreatorNotFound(val userId: Long) : DreamCreationErrors()
  object PersistenceError : DreamCreationErrors()
}

sealed class TagCreationErrors : DreamCreationErrors() {
  object TitleMissing : TagCreationErrors()
  object TitleTooLong : TagCreationErrors()
  object CreationError : TagCreationErrors()
  object PersistenceError : TagCreationErrors()
}
