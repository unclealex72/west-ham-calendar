package security

/**
  * A class used to determine is SSL is required.
  * Created by alex on 17/03/16.
  */
case class RequireSSL(val requireSSL: Boolean) {

  def apply(): Boolean = requireSSL
}
