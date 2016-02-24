package calendar

import com.greencatsoft.angularjs.{Filter, FilterService, injectable}

import scala.scalajs.js

/**
  * Created by alex on 21/02/16.
  */
@injectable("opponents")
class OpponentsFilter extends Filter[js.Array[HasOpponents]] {

  override def filter(items: js.Array[HasOpponents]): js.Array[HasOpponents] = null

  override def filter(items: js.Array[HasOpponents], args: Seq[Any]): js.Array[HasOpponents] = {
    args.headOption.filter(_.isInstanceOf[String]).map(_.asInstanceOf[String]) match {
      case Some("") => items
      case Some(prefix) =>
        items.filter(_.hasOpponents(prefix))
      case None => items
    }
  }
}
