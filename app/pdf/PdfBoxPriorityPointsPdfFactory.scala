package pdf

import java.io.{ByteArrayInputStream, OutputStream}
import java.util.Base64
import javax.imageio.ImageIO
import javax.inject.Inject

import com.typesafe.scalalogging.slf4j.StrictLogging
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import org.apache.pdfbox.pdmodel.{PDDocument, PDPageContentStream}
import pdf.ClientType.{Junior, OAP}
import pdf.ContentStream._

/**
 * Created by alex on 08/02/15.
 */
class PdfBoxPriorityPointsPdfFactory @Inject() (pdfPositioning: PdfPositioning) extends PriorityPointsPdfFactory with App with StrictLogging {

  override def generate(priorityPointsConfiguration: PriorityPointsConfiguration, team: String, league: Boolean, clientFilter: Client => Boolean, out: OutputStream): Unit = {
    try {
     doGenerate(priorityPointsConfiguration, team, league, clientFilter, out)
    }
    catch {
      case e: Exception =>
        logger.error("Cannot generate a priority points pdf form.", e)
        throw e
    }
  }

  def doGenerate(priorityPointsConfiguration: PriorityPointsConfiguration, team: String, league: Boolean, clientFilter: Client => Boolean, out: OutputStream): Unit = {
    val in = classOf[PdfBoxPriorityPointsPdfFactory].getResourceAsStream("prioritypoints.pdf")
    val document = PDDocument.load(in)
    in.close()
    val page = document.getPage(0)
    val contentStream = new PDPageContentStream(document, page, true, true)

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

    // Add signature

    val imageData = Base64.getDecoder.decode(priorityPointsConfiguration.signature)
    val bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData))
    val pdImage = LosslessFactory.createFromImage(document, bufferedImage)
    val width: Float = pdImage.getWidth
    val height: Float = pdImage.getHeight
    val fixedHeight: Float = pdfPositioning.signatureHeight

    contentStream.drawImage(
      pdImage,
      pdfPositioning.signaturePosition.x,
      pdfPositioning.signaturePosition.y,
      width * (fixedHeight / height),
      fixedHeight)

    // Write the document
    contentStream.close()
    document.save(out)
    document.close()

  }
}

object ContentStream {

  implicit class ContentStreamExtensions(cs: PDPageContentStream) {

  val font = PDType1Font.HELVETICA_BOLD

  def write(text: Any, position: Point) = {
    cs.beginText()
    cs.setFont(font, 12)
    cs.newLineAtOffset(position.x, position.y)
    cs.showText(text.toString)
    cs.endText()
  }

  def write(text: Any, position: RepeatingPoint): Unit = {
    text.toString.toCharArray.zipWithIndex.foreach {
      case (ch, idx) =>
        write(ch, position.point.rightBy(idx * position.offset))
    }
  }

  def strikeOut(box: Box) = {
    cs.addRect(box.bottomLeft.x, box.bottomLeft.y, box.size.width, box.size.height)
    cs.fill()
  }
}
}

