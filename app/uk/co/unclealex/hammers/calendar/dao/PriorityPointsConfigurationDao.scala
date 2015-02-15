package uk.co.unclealex.hammers.calendar.dao

import pdf.PriorityPointsConfiguration

/**
 * Created by alex on 15/02/15.
 */
trait PriorityPointsConfigurationDao {

  /**
   * Get the configuration for priority point PDF forms.
   * @return
   */
  def get: Option[PriorityPointsConfiguration]
}
