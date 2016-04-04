package calendar.directives

/**
  * A trait that knows where templates are located.
  * Created by alex on 01/04/16.
  */
trait TemplateLocation {

  def at(location: String) = s"/assets/templates/$location"
}
