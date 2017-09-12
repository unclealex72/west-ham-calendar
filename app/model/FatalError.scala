package model

import java.time.ZonedDateTime

/**
  * Created by alex on 01/03/16.
  */
case class FatalError(id: Long, at: ZonedDateTime, message: String)