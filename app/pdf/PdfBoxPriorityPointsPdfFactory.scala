package pdf

import java.io.{FileOutputStream, OutputStream}
import javax.inject.Inject

import com.typesafe.scalalogging.slf4j.StrictLogging
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.{PDDocument, PDPage}
import uk.co.unclealex.hammers.calendar.dao.PriorityPointsConfigurationDao
import uk.co.unclealex.hammers.calendar.model.PersistedClientType.{Junior, OAP}

import scala.sys.process._
/**
 * Created by alex on 08/02/15.
 */
class PdfBoxPriorityPointsPdfFactory @Inject() (pdfPositioning: PdfPositioning, priorityPointsConfigurationDao: PriorityPointsConfigurationDao) extends PriorityPointsPdfFactory with App with StrictLogging {

  override def generate(team: String, league: Boolean, clientFilter: Client => Boolean, out: OutputStream): Unit = {
    try {
     doGenerate(team, league, clientFilter, out)
    }
    catch {
      case e: Exception => {
        logger.error("Cannot generate a priority points pdf form.", e)
        throw e
      }
    }
  }

  def doGenerate(team: String, league: Boolean, clientFilter: Client => Boolean, out: OutputStream): Unit = {
    val priorityPointsConfiguration = priorityPointsConfigurationDao.get.getOrElse {
      throw new IllegalStateException("No configuration for the priorty points PDF form has been set.")
    }

    val url = classOf[PdfBoxPriorityPointsPdfFactory].getResource("prioritypoints.pdf")
    val document = PDDocument.load(url)
    val page = document.getDocumentCatalog.getAllPages.get(0).asInstanceOf[PDPage]
    val contentStream = new PDPageContentStream(document, page, true, true) with ContentStreamExtensions
    page.getContents.getStream

    // Write the team name
    contentStream.write(team.toUpperCase, pdfPositioning.team)

    // Block out either the league or cup text
    val strikeOutBox = if (league) pdfPositioning.cup else pdfPositioning.league
    contentStream.strikeOut(strikeOutBox)

    val clients = priorityPointsConfiguration.clients.filter(clientFilter)

    // Print out the lead client's name and reference
    clients.headOption.foreach { client =>
      contentStream.write(client.name.toUpperCase, pdfPositioning.name)
      contentStream.write(client.referenceNumber, pdfPositioning.ref)
    }

    // Main address
    contentStream.write(priorityPointsConfiguration.contactDetails.address.toUpperCase, pdfPositioning.address)

    // Telephone numbers
    contentStream.write(priorityPointsConfiguration.contactDetails.daytimeTelephoneNumber, pdfPositioning.daytimePhone)
    contentStream.write(priorityPointsConfiguration.contactDetails.mobilePhoneNumber, pdfPositioning.mobilePhone)

    // Email address
    contentStream.write(priorityPointsConfiguration.contactDetails.emailAddress, pdfPositioning.emailAddress)

    // Full client information
    clients.zipWithIndex foreach {
      case (client, idx) =>
        val clientRefPoint = pdfPositioning.clientRef.downBy(pdfPositioning.nextClientOffset * idx)
        contentStream.write(client.referenceNumber, clientRefPoint)
        contentStream.write(client.name.toUpperCase, clientRefPoint.rightBy(pdfPositioning.clientNameOffset))
        client.clientType.foreach { clientType =>
          val offset = clientType match {
            case Junior => pdfPositioning.juniorOffset
            case OAP => pdfPositioning.oapOffset
          }
          contentStream.write("X", clientRefPoint.rightBy(offset))
        }
    }

    // Credit card information
    val card = priorityPointsConfiguration.creditCard
    contentStream.write(card.number, pdfPositioning.creditCard)
    contentStream.write(f"${card.expiry.month}%02d ${card.expiry.year % 100}%02d", pdfPositioning.expiryDate)
    contentStream.write(card.securityCode, pdfPositioning.securityCode)
    contentStream.write(card.nameOnCard.toUpperCase, pdfPositioning.nameOnCard)

    // Number of clients
    contentStream.write(clients.size, pdfPositioning.numberOfTickets)

    // Write the document
    contentStream.close()
    document.save(out)
    document.close()

  }
}

trait ContentStreamExtensions {

  self: PDPageContentStream =>

  val font = PDType1Font.HELVETICA_BOLD

  def write(text: Any, position: Point) = {
    self.beginText()
    self.setFont(font, 12)
    self.moveTextPositionByAmount(position.x, position.y)
    self.drawString(text.toString)
    self.endText()
  }

  def write(text: Any, position: RepeatingPoint): Unit = {
    text.toString.toCharArray.zipWithIndex.foreach {
      case (ch, idx) =>
        write(ch, position.point.rightBy(idx * position.offset))
    }
  }

  def strikeOut(box: Box) = {
    self.fillRect(box.bottomLeft.x, box.bottomLeft.y, box.size.width, box.size.height)
  }
}
