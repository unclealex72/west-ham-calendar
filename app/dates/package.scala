import java.time.ZonedDateTime

import models.GameRow

/**
  * Created by alex on 10/07/17
  **/
package object dates {

  type JvmGameRow = GameRow[ZonedDateTime]
}
