package com.infusionvlc.somniumserver.dreams.models

sealed class DreamRemovalErrors {
  class DreamNotFound(val id: Long) : DreamRemovalErrors()
  object UserIsNotCreator : DreamRemovalErrors()
}
