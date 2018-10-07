package com.infusionvlc.somniumserver.dreams.models

sealed class DreamDetailErrors {
  class DreamNotFound(val id: Long) : DreamDetailErrors()
  object DreamIsNotPublic : DreamDetailErrors()
}
