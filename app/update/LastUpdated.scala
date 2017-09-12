package update

import java.time.ZonedDateTime

/**
 * A trait that allows the the time calendars were last updated to be persisted and retreived.
 */
trait LastUpdated {

  /**
   * Set the time the calendars were last updated.
   */
  def at(lastUpdatedTime : ZonedDateTime): Unit
  
  /**
   * Get the time the calendars were last updated.
   */
  def when : Option[ZonedDateTime]
}