package dao

import pdf.PriorityPointsConfiguration

import scala.concurrent.Future

/**
 * Created by alex on 15/02/15.
 */
trait PriorityPointsConfigurationDao {

  /**
   * Get the configuration for priority point PDF forms.
 *
   * @return
   */
  def get: Future[Option[PriorityPointsConfiguration]]
}
