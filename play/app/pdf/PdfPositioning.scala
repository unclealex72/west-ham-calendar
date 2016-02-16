package pdf

/**
 * Created by alex on 08/02/15.
 */
case class PdfPositioning(team: Point, league: Box, cup: Box, name: Point, ref: Point, address: Point,
                           daytimePhone: Point, mobilePhone: Point, emailAddress: Point, clientRef: Point, clientNameOffset: Int,
                           oapOffset: Int, juniorOffset: Int, nextClientOffset: Int,
                           creditCard: RepeatingPoint,
                           expiryDate: RepeatingPoint, securityCode: RepeatingPoint,
                           nameOnCard: Point, numberOfTickets: Point,
                          signaturePosition: Point, signatureHeight: Int)


case class Point(x: Int, y: Int) {
  def downBy(i: Int) = Point(x, y - i)
  def upBy(i: Int) = Point(x, y + i)
  def leftBy(i: Int) = Point(x - i, y)
  def rightBy(i: Int) = Point(x + i, y)
}

case class RepeatingPoint(point: Point, offset: Int)

case class Dimension(width: Int, height: Int)

case class Box(bottomLeft: Point, size: Dimension)