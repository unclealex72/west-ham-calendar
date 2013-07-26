package uk.co.unclealex.hammers.calendar.update

import org.joda.time.DateTime

/**
 * A trait that allows the the time calendars were last updated to be persisted and retreived.
 */
trait LastUpdated {

  /**
   * Set the time the calendars were last updated.
   */
  def at(lastUpdatedTime : DateTime): Unit
  
  /**
   * Get the time the calendars were last updated.
   */
  def when : Option[DateTime]
}