package com.infusionvlc.somniumserver.tags.models

sealed class TagCreationErrors {
  object TitleMissing : TagCreationErrors()
  object TitleTooLong : TagCreationErrors()
  object CreationError : TagCreationErrors()
  object PersistenceError: TagCreationErrors()
}
