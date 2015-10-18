package pdf

import java.io.OutputStream

/**
 * A trait for classes that fill in the details on the priority points
 * Created by alex on 08/02/15.
 */
trait PriorityPointsPdfFactory {

  def generate(team: String, league: Boolean, clientFilter: Client => Boolean, out: OutputStream): Unit
}
