import java.io.File

import org.htmlcleaner.{HtmlCleaner, SimpleXmlSerializer}

import scala.io.Source
import scala.xml.XML

/**
  * Created by alex on 31/10/16.
  */
object QuickApp extends App{

  val body = Source.fromFile(new File("/home/alex/git/west-ham-calendar/test/resources/html/Tickets/Match-Tickets/Away-Matches/Nov-27-Manchester-United-v-West-Ham-United.html")).getLines().mkString("\n")
  val cleaner = new HtmlCleaner()
  val rootNode = cleaner.clean(body)
  val page = new SimpleXmlSerializer(cleaner.getProperties).getAsString(rootNode)
  val xml = XML.loadString(page)
  val texts = xml.descendant_or_self.map(_.text)
  texts.foreach(text => println(text))


}
