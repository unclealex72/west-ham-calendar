package calendar.services

import com.greencatsoft.angularjs.{Factory, Service, injectable}

import scala.scalajs.js

/**
  * Created by alex on 09/04/16.
  */
@injectable("watcher")
class WatcherService extends Service {

  def apply[A](watcher: A => A => js.Any): js.Function = {
    new js.Function2[A, A, js.Any] {
      override def apply(newValue: A, oldValue: A): js.Any = {
        if (newValue != oldValue) watcher(newValue)(oldValue) else false
      }
    }
  }
}


@injectable("watcher")
class WatcherServiceFactory extends Factory[WatcherService] {

  override def apply(): WatcherService = new WatcherService
}