package model

import org.joda.time.DateTime

/**
  * Created by alex on 01/03/16.
  */
case class FatalError(id: Long, at: DateTime, message: String)