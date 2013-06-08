/**
 * Copyright 2013 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with work for additional information
 * regarding copyright ownership.  The ASF licenses file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package uk.co.unclealex.hammers.calendar.geo

import scala.math.BigDecimal
import com.rockymadden.stringmetric.similarity.WeightedLevenshteinMetric
import com.typesafe.scalalogging.slf4j.Logging
import scala.collection.immutable.SortedMap
import com.rockymadden.stringmetric.similarity.JaroWinklerMetric

/**
 * Geographic locations of all UK football league stadia
 * @author alex
 *
 */
trait GeoLocationLike {

  trait Value { self: GeoLocation =>

    /**
     *  The index used for ordering.
     */
    val index = _values.keySet.size
    _values += (this.team.toLowerCase -> this)

  }

  /**
   * A list of all the registered instances of this type.
   */
  private var _values = SortedMap.empty[String, GeoLocation]
  def values: SortedMap[String, GeoLocation] = _values

}

sealed abstract class GeoLocation(
  /**
   * The name of the team who play at the ground.
   */
  val team: String,
  /**
   * The name of the ground.
   */
  val name: String, 
  /**
   * The latitude of the ground.
   */
  val latitude: BigDecimal, 
  /**
   * The longitude of the ground.
   */
  val longitude: BigDecimal) extends GeoLocation.Value
object GeoLocation extends GeoLocationLike {

  case object WYCOMBE_WANDERERS extends GeoLocation("Wycombe Wanderers", "Adams Park", BigDecimal("51.6306"), BigDecimal("-0.800299")); WYCOMBE_WANDERERS
  case object LIVINGSTON extends GeoLocation("Livingston", "Almondvale Stadium", BigDecimal("55.8864"), BigDecimal("-3.52207")); LIVINGSTON
  case object BRIGHTON_AND_HOVE_ALBION extends GeoLocation("Brighton and Hove Albion", "Amex Stadium", BigDecimal("50.8609"), BigDecimal("-0.08014")); BRIGHTON_AND_HOVE_ALBION
  case object LIVERPOOL extends GeoLocation("Liverpool", "Anfield", BigDecimal("53.4308"), BigDecimal("-2.96096")); LIVERPOOL
  case object BRISTOL_CITY extends GeoLocation("Bristol City", "Ashton Gate", BigDecimal("51.44"), BigDecimal("-2.62021")); BRISTOL_CITY
  case object CHESTERFIELD extends GeoLocation("Chesterfield", "B2net Stadium", BigDecimal("53.2535"), BigDecimal("-1.4272")); CHESTERFIELD
  case object PETERHEAD extends GeoLocation("Peterhead", "Balmoor", BigDecimal("57.5112"), BigDecimal("-1.79599")); PETERHEAD
  case object EAST_FIFE extends GeoLocation("East Fife", "Bayview Stadium", BigDecimal("56.1889"), BigDecimal("-2.99914")); EAST_FIFE
  case object WALSALL extends GeoLocation("Walsall", "Bescot Stadium", BigDecimal("52.5655"), BigDecimal("-1.99053")); WALSALL
  case object BLACKPOOL extends GeoLocation("Blackpool", "Bloomfield Road", BigDecimal("53.8046"), BigDecimal("-3.04834")); BLACKPOOL
  case object WEST_HAM extends GeoLocation("West Ham", "Boleyn Ground", BigDecimal("51.5321"), BigDecimal("0.039225")); WEST_HAM
  case object YORK_CITY extends GeoLocation("York City", "Bootham Crescent", BigDecimal("53.9695"), BigDecimal("-1.08833")); YORK_CITY
  case object ELGIN_CITY extends GeoLocation("Elgin City", "Borough Briggs", BigDecimal("57.6518"), BigDecimal("-3.3209")); ELGIN_CITY
  case object OLDHAM_ATHLETIC extends GeoLocation("Oldham Athletic", "Boundary Park", BigDecimal("53.5551"), BigDecimal("-2.12847")); OLDHAM_ATHLETIC
  case object SHEFFIELD_UNITED extends GeoLocation("Sheffield United", "Bramall Lane", BigDecimal("53.3703"), BigDecimal("-1.47083")); SHEFFIELD_UNITED
  case object LEYTON_ORIENT extends GeoLocation("Leyton Orient", "Brisbane Road", BigDecimal("51.5601"), BigDecimal("-0.012551")); LEYTON_ORIENT
  case object STOKE_CITY extends GeoLocation("Stoke City", "Britannia Stadium", BigDecimal("52.9884"), BigDecimal("-2.17542")); STOKE_CITY
  case object CRAWLEY_TOWN extends GeoLocation("Crawley Town", "Broadfield Stadium", BigDecimal("51.0996"), BigDecimal("-0.194579")); CRAWLEY_TOWN
  case object CLYDE extends GeoLocation("Clyde", "Broadwood Stadium", BigDecimal("55.9447"), BigDecimal("-4.0379")); CLYDE
  case object CARLISLE_UNITED extends GeoLocation("Carlisle United", "Brunton Park", BigDecimal("54.8955"), BigDecimal("-2.91365")); CARLISLE_UNITED
  case object GREENOCK_MORTON extends GeoLocation("Greenock Morton", "Cappielow Park", BigDecimal("55.9414"), BigDecimal("-4.72719")); GREENOCK_MORTON
  case object CARDIFF_CITY extends GeoLocation("Cardiff City", "Cardiff City Stadium", BigDecimal("51.4729"), BigDecimal("-3.20413")); CARDIFF_CITY
  case object NORWICH_CITY extends GeoLocation("Norwich City", "Carrow Road", BigDecimal("52.6221"), BigDecimal("1.30912")); NORWICH_CITY
  case object CELTIC extends GeoLocation("Celtic", "Celtic Park", BigDecimal("55.8497"), BigDecimal("-4.20561")); CELTIC
  case object COEDENBEATH extends GeoLocation("Coedenbeath", "Central Park", BigDecimal("56.1088"), BigDecimal("-3.34717")); COEDENBEATH
  case object ALBION_ROVERS extends GeoLocation("Albion Rovers", "Cliftonhill", BigDecimal("55.8602"), BigDecimal("-4.01145")); ALBION_ROVERS
  case object FULHAM extends GeoLocation("Fulham", "Craven Cottage", BigDecimal("51.4749"), BigDecimal("-0.221619")); FULHAM
  case object ACCRINGTON_STANLEY extends GeoLocation("Accrington Stanley", "Crown Ground", BigDecimal("53.7654"), BigDecimal("-2.37106")); ACCRINGTON_STANLEY
  case object PRESTON_NORTH_END extends GeoLocation("Preston North End", "Deepdale", BigDecimal("53.772"), BigDecimal("-2.68832")); PRESTON_NORTH_END
  case object DUNDEE extends GeoLocation("Dundee", "Dens Park", BigDecimal("56.4747"), BigDecimal("-2.97352")); DUNDEE
  case object DUMBARTON extends GeoLocation("Dumbarton", "Dumbarton Football Stadium", BigDecimal("55.9384"), BigDecimal("-4.56162")); DUMBARTON
  case object WIGAN_ATHLETIC extends GeoLocation("Wigan Athletic", "DW Stadium", BigDecimal("53.5477"), BigDecimal("-2.65415")); WIGAN_ATHLETIC
  case object DUNFERMLINE extends GeoLocation("Dunfermline", "East End Park", BigDecimal("56.0756"), BigDecimal("-3.44196")); DUNFERMLINE
  case object HIBERNIAN extends GeoLocation("Hibernian", "Easter Road", BigDecimal("55.9616"), BigDecimal("-3.16521")); HIBERNIAN
  case object HEREFORD_UNITED extends GeoLocation("Hereford United", "Edgar Street", BigDecimal("52.0607"), BigDecimal("-2.71774")); HEREFORD_UNITED
  case object LEEDS_UNITED extends GeoLocation("Leeds United", "Elland Road", BigDecimal("53.7775"), BigDecimal("-1.57212")); LEEDS_UNITED
  case object ARSENAL extends GeoLocation("Arsenal", "Emirates Stadium", BigDecimal("51.5549"), BigDecimal("-0.108436")); ARSENAL
  case object MANCHESTER_CITY extends GeoLocation("Manchester City", "Etihad Stadium", BigDecimal("53.483"), BigDecimal("-2.20024")); MANCHESTER_CITY
  case object BLACKBURN_ROVERS extends GeoLocation("Blackburn Rovers", "Ewood Park", BigDecimal("53.7286"), BigDecimal("-2.48937")); BLACKBURN_ROVERS
  case object FALKIRK extends GeoLocation("Falkirk", "Falkirk Stadium", BigDecimal("56.0053"), BigDecimal("-3.75262")); FALKIRK
  case object MOTHERWELL extends GeoLocation("Motherwell", "Fir Park", BigDecimal("55.7804"), BigDecimal("-3.9803")); MOTHERWELL
  case object PARTICK_THISTLE extends GeoLocation("Partick Thistle", "Firhill", BigDecimal("55.8815"), BigDecimal("-4.26938")); PARTICK_THISTLE
  case object STIRLING_ALBION extends GeoLocation("Stirling Albion", "Forthbank Stadium", BigDecimal("56.1191"), BigDecimal("-3.91194")); STIRLING_ALBION
  case object PORTSMOUTH extends GeoLocation("Portsmouth", "Fratton Park", BigDecimal("50.7964"), BigDecimal("-1.06389")); PORTSMOUTH
  case object ANNAN_ATHLETIC extends GeoLocation("Annan Athletic", "Galabank", BigDecimal("54.9948"), BigDecimal("-3.2612")); ANNAN_ATHLETIC
  case object ARBROATH extends GeoLocation("Arbroath", "Gayfield Park", BigDecimal("56.5523"), BigDecimal("-2.5914")); ARBROATH
  case object BURY extends GeoLocation("Bury", "Gigg Lane", BigDecimal("53.5805"), BigDecimal("-2.29487")); BURY
  case object SCUNTHORPE_UNITED extends GeoLocation("Scunthorpe United", "Glanford Park", BigDecimal("53.5867"), BigDecimal("-0.695244")); SCUNTHORPE_UNITED
  case object BRECHIN_CITY extends GeoLocation("Brechin City", "Glebe Park", BigDecimal("56.7353"), BigDecimal("-2.6565")); BRECHIN_CITY
  case object MORECAMBE extends GeoLocation("Morecambe", "Globe Arena", BigDecimal("54.0675"), BigDecimal("-2.84707")); MORECAMBE
  case object EVERTON extends GeoLocation("Everton", "Goodison Park", BigDecimal("53.4387"), BigDecimal("-2.96619")); EVERTON
  case object CREWE_ALEXANDRA extends GeoLocation("Crewe Alexandra", "Gresty Road", BigDecimal("53.0875"), BigDecimal("-2.43569")); CREWE_ALEXANDRA
  case object BRENTFORD extends GeoLocation("Brentford", "Griffin Park", BigDecimal("51.4882"), BigDecimal("-0.302621")); BRENTFORD
  case object QUEENS_PARK extends GeoLocation("Queen's Park", "Hampden Park", BigDecimal("55.8258"), BigDecimal("-4.25198")); QUEENS_PARK
  case object SCOTLAND extends GeoLocation("Scotland", "Hampden Park", BigDecimal("55.8258"), BigDecimal("-4.25198")); SCOTLAND
  case object FLEETWOOD_TOWN extends GeoLocation("Fleetwood Town", "Highbury Stadium", BigDecimal("53.9165"), BigDecimal("-3.02484")); FLEETWOOD_TOWN
  case object SHEFFIELD_WEDNESDAY extends GeoLocation("Sheffield Wednesday", "Hillsborough", BigDecimal("53.4115"), BigDecimal("-1.50075")); SHEFFIELD_WEDNESDAY
  case object PLYMOUTH_ARGYLE extends GeoLocation("Plymouth Argyle", "Home Park", BigDecimal("50.3882"), BigDecimal("-4.15076")); PLYMOUTH_ARGYLE
  case object YEOVIL_TOWN extends GeoLocation("Yeovil Town", "Huish Park", BigDecimal("50.9503"), BigDecimal("-2.67383")); YEOVIL_TOWN
  case object RANGERS extends GeoLocation("Rangers", "Ibrox", BigDecimal("55.8529"), BigDecimal("-4.30962")); RANGERS
  case object OXFORD_UNITED extends GeoLocation("Oxford United", "Kassam Stadium", BigDecimal("51.7164"), BigDecimal("-1.20775")); OXFORD_UNITED
  case object HULL_CITY extends GeoLocation("Hull City", "KC Stadium", BigDecimal("53.7465"), BigDecimal("-0.368009")); HULL_CITY
  case object DONCASTER_ROVERS extends GeoLocation("Doncaster Rovers", "Keepmoat Stadium", BigDecimal("53.5099"), BigDecimal("-1.11382")); DONCASTER_ROVERS
  case object LEICESTER_CITY extends GeoLocation("Leicester City", "King Power Stadium", BigDecimal("52.6203"), BigDecimal("-1.14217")); LEICESTER_CITY
  case object AFC_WIMBLEDON extends GeoLocation("AFC Wimbledon", "Kingsmeadow", BigDecimal("51.4051"), BigDecimal("-0.281984")); AFC_WIMBLEDON
  case object KINGSTONIAN extends GeoLocation("Kingstonian", "Kingsmeadow", BigDecimal("51.4051"), BigDecimal("-0.281984")); KINGSTONIAN
  case object SWANSEA_CITY extends GeoLocation("Swansea City", "Liberty Stadium", BigDecimal("51.6428"), BigDecimal("-3.93473")); SWANSEA_CITY
  case object MONTROSE extends GeoLocation("Montrose", "Links Park", BigDecimal("56.714"), BigDecimal("-2.45902")); MONTROSE
  case object QUEENS_PARK_RANGERS extends GeoLocation("Queens Park Rangers", "Loftus Road", BigDecimal("51.5093"), BigDecimal("-0.232204")); QUEENS_PARK_RANGERS
  case object PETERBOROUGH_UNITED extends GeoLocation("Peterborough United", "London Road", BigDecimal("52.5648"), BigDecimal("-0.240434")); PETERBOROUGH_UNITED
  case object READING extends GeoLocation("Reading", "Madjeski Stadium", BigDecimal("51.4222"), BigDecimal("-0.982777")); READING
  case object ST_JOHNSTONE extends GeoLocation("St Johnstone", "McDiarmid Park", BigDecimal("56.4099"), BigDecimal("-3.47684")); ST_JOHNSTONE
  case object NOTTS_COUNTY extends GeoLocation("Notts County", "Meadow Lane", BigDecimal("52.9425"), BigDecimal("-1.13703")); NOTTS_COUNTY
  case object BRISTOL_ROVERS extends GeoLocation("Bristol Rovers", "Memorial Stadium", BigDecimal("51.4862"), BigDecimal("-2.58315")); BRISTOL_ROVERS
  case object WALES extends GeoLocation("Wales", "Millennium Stadium", BigDecimal("51.4782"), BigDecimal("-3.18281")); WALES
  case object WOLVERHAMPTON_WANDERERS extends GeoLocation("Wolverhampton Wanderers", "Molineux", BigDecimal("52.5904"), BigDecimal("-2.13061")); WOLVERHAMPTON_WANDERERS
  case object MACCLESFIELD_TOWN extends GeoLocation("Macclesfield Town", "Moss Rose", BigDecimal("53.2427"), BigDecimal("-2.127")); MACCLESFIELD_TOWN
  case object AIRDRIE_UNITED extends GeoLocation("Airdrie United", "New Broomfield", BigDecimal("55.8601"), BigDecimal("-3.95997")); AIRDRIE_UNITED
  case object HAMILTON_ACADEMICAL extends GeoLocation("Hamilton Academical", "New Douglas Park", BigDecimal("55.7821"), BigDecimal("-4.058")); HAMILTON_ACADEMICAL
  case object SHREWSBURY_TOWN extends GeoLocation("Shrewsbury Town", "New Meadow", BigDecimal("52.6886"), BigDecimal("-2.74931")); SHREWSBURY_TOWN
  case object ROTHERHAM_UNITED extends GeoLocation("Rotherham United", "New York Stadiumn", BigDecimal("53.4281"), BigDecimal("-1.36172")); ROTHERHAM_UNITED
  case object BARNSLEY extends GeoLocation("Barnsley", "Oakwell", BigDecimal("53.5524"), BigDecimal("-1.46756")); BARNSLEY
  case object EAST_STIRLINGSHIRE extends GeoLocation("East Stirlingshire", "Ochilview Park", BigDecimal("56.0282"), BigDecimal("-3.81449")); EAST_STIRLINGSHIRE
  case object STENHOUSEMUIR extends GeoLocation("Stenhousemuir", "Ochilview Park", BigDecimal("56.0282"), BigDecimal("-3.81449")); STENHOUSEMUIR
  case object MANCHESTER_UNITED extends GeoLocation("Manchester United", "Old Trafford", BigDecimal("53.4631"), BigDecimal("-2.29139")); MANCHESTER_UNITED
  case object QUEEN_OF_THE_SOUTH extends GeoLocation("Queen of the South", "Palmerston Park", BigDecimal("55.0703"), BigDecimal("-3.6246")); QUEEN_OF_THE_SOUTH
  case object BURTON_ALBION extends GeoLocation("Burton Albion", "Pirelli Stadium", BigDecimal("52.8219"), BigDecimal("-1.62708")); BURTON_ALBION
  case object ABERDEEN extends GeoLocation("Aberdeen", "Pittodrie", BigDecimal("57.1593"), BigDecimal("-2.08872")); ABERDEEN
  case object TORQUAY_UNITED extends GeoLocation("Torquay United", "Plainmoor", BigDecimal("50.4764"), BigDecimal("-3.52382")); TORQUAY_UNITED
  case object IPSWICH_TOWN extends GeoLocation("Ipswich Town", "Portman Road", BigDecimal("52.0544"), BigDecimal("1.14554")); IPSWICH_TOWN
  case object TRANMERE_ROVERS extends GeoLocation("Tranmere Rovers", "Prenton Park", BigDecimal("53.3738"), BigDecimal("-3.03269")); TRANMERE_ROVERS
  case object DERBY_COUNTY extends GeoLocation("Derby County", "Pride Park", BigDecimal("52.9149"), BigDecimal("-1.44727")); DERBY_COUNTY
  case object GILLINGHAM extends GeoLocation("Gillingham", "Priestfield Stadium", BigDecimal("51.3844"), BigDecimal("0.560367")); GILLINGHAM
  case object ALDERSHOT_TOWN extends GeoLocation("Aldershot Town", "Recreation Ground", BigDecimal("51.2484"), BigDecimal("-0.754869")); ALDERSHOT_TOWN
  case object ALLOA_ATHLETIC extends GeoLocation("Alloa Athletic", "Recreation Park", BigDecimal("56.1166"), BigDecimal("-3.77865")); ALLOA_ATHLETIC
  case object BOLTON_WANDERERS extends GeoLocation("Bolton Wanderers", "Reebok Stadium", BigDecimal("53.5805"), BigDecimal("-2.53571")); BOLTON_WANDERERS
  case object COVENTRY_CITY extends GeoLocation("Coventry City", "Ricoh Arena", BigDecimal("52.4481"), BigDecimal("-1.49563")); COVENTRY_CITY
  case object MIDDLESBROUGH extends GeoLocation("Middlesbrough", "Riverside", BigDecimal("54.5781"), BigDecimal("-1.21776")); MIDDLESBROUGH
  case object SOUTHEND_UNITED extends GeoLocation("Southend United", "Roots Hall", BigDecimal("51.5491"), BigDecimal("0.701572")); SOUTHEND_UNITED
  case object KILMARNOCK extends GeoLocation("Kilmarnock", "Rugby Park", BigDecimal("55.6046"), BigDecimal("-4.50846")); KILMARNOCK
  case object CRYSTAL_PALACE extends GeoLocation("Crystal Palace", "Selhurst Park", BigDecimal("51.3983"), BigDecimal("-0.085455")); CRYSTAL_PALACE
  case object BOURNEMOUTH extends GeoLocation("Bournemouth", "Seward Stadium", BigDecimal("50.7352"), BigDecimal("-1.83839")); BOURNEMOUTH
  case object BERWICK_RANGERS extends GeoLocation("Berwick Rangers", "Shielfield Park", BigDecimal("55.76"), BigDecimal("-2.01599")); BERWICK_RANGERS
  case object NORTHAMPTON_TOWN extends GeoLocation("Northampton Town", "Sixfields Stadium", BigDecimal("52.2352"), BigDecimal("-0.933485")); NORTHAMPTON_TOWN
  case object AYR_UNITED extends GeoLocation("Ayr United", "Somerset Park", BigDecimal("55.4697"), BigDecimal("-4.61996")); AYR_UNITED
  case object ROCHDALE extends GeoLocation("Rochdale", "Spotland Stadium", BigDecimal("53.6209"), BigDecimal("-2.17993")); ROCHDALE
  case object BIRMINGHAM_CITY extends GeoLocation("Birmingham City", "St Andrews", BigDecimal("52.4756"), BigDecimal("-1.86824")); BIRMINGHAM_CITY
  case object NEWCASTLE_UNITED extends GeoLocation("Newcastle United", "St James Park", BigDecimal("54.9756"), BigDecimal("-1.62179")); NEWCASTLE_UNITED
  case object EXETER_CITY extends GeoLocation("Exeter City", "St James Park, Exeter", BigDecimal("50.7307"), BigDecimal("-3.52109")); EXETER_CITY
  case object SOUTHAMPTON extends GeoLocation("Southampton", "St Mary's Stadium", BigDecimal("50.9058"), BigDecimal("-1.39114")); SOUTHAMPTON
  case object ST_MIRREN extends GeoLocation("St Mirren", "St Mirren Park", BigDecimal("55.8529"), BigDecimal("-4.42879")); ST_MIRREN
  case object SUNDERLAND extends GeoLocation("Sunderland", "Stadium of Light", BigDecimal("54.9146"), BigDecimal("-1.38837")); SUNDERLAND
  case object MK_DONS extends GeoLocation("MK Dons", "Stadiummk", BigDecimal("52.0096"), BigDecimal("-0.733507")); MK_DONS
  case object STRANRAER extends GeoLocation("Stranraer", "Stair Park", BigDecimal("54.9022"), BigDecimal("-5.01249")); STRANRAER
  case object CHELSEA extends GeoLocation("Chelsea", "Stamford Bridge", BigDecimal("51.4816"), BigDecimal("-0.191034")); CHELSEA
  case object RAITH_ROVERS extends GeoLocation("Raith Rovers", "Stark's Park", BigDecimal("56.0999"), BigDecimal("-3.16851")); RAITH_ROVERS
  case object FORFAR_ATHLETIC extends GeoLocation("Forfar Athletic", "Station Park", BigDecimal("56.6523"), BigDecimal("-2.88492")); FORFAR_ATHLETIC
  case object DUNDEE_UNITED extends GeoLocation("Dundee United", "Tannadice Park", BigDecimal("56.4748"), BigDecimal("-2.96902")); DUNDEE_UNITED
  case object INVERNESS_CALEDONIAN_THISTLE extends GeoLocation("Inverness Caledonian Thistle", "The Caledonian Stadium", BigDecimal("57.4951"), BigDecimal("-4.21751")); INVERNESS_CALEDONIAN_THISTLE
  case object NOTTINGHAM_FOREST extends GeoLocation("Nottingham Forest", "The City Ground", BigDecimal("52.9399"), BigDecimal("-1.13258")); NOTTINGHAM_FOREST
  case object SWINDON_TOWN extends GeoLocation("Swindon Town", "The County Ground", BigDecimal("51.5645"), BigDecimal("-1.77107")); SWINDON_TOWN
  case object MILLWALL extends GeoLocation("Millwall", "The Den", BigDecimal("51.4859"), BigDecimal("-0.050743")); MILLWALL
  case object HUDDERSFIELD_TOWN extends GeoLocation("Huddersfield Town", "The Galpharm Stadium", BigDecimal("53.6543"), BigDecimal("-1.76837")); HUDDERSFIELD_TOWN
  case object WEST_BROMWICH_ALBION extends GeoLocation("West Bromwich Albion", "The Hawthorns", BigDecimal("52.509"), BigDecimal("-1.96418")); WEST_BROMWICH_ALBION
  case object STEVENAGE_BOROUGH extends GeoLocation("Stevenage Borough", "The Lamex Stadium", BigDecimal("51.8898"), BigDecimal("-0.193664")); STEVENAGE_BOROUGH
  case object CHARLTON_ATHLETIC extends GeoLocation("Charlton Athletic", "The Valley", BigDecimal("51.4865"), BigDecimal("0.036757")); CHARLTON_ATHLETIC
  case object BURNLEY extends GeoLocation("Burnley", "Turf Moor", BigDecimal("53.7888"), BigDecimal("-2.23018")); BURNLEY
  case object HEART_OF_MIDLOTHIAN extends GeoLocation("Heart of Midlothian", "Tynecastle", BigDecimal("55.9388"), BigDecimal("-3.2325")); HEART_OF_MIDLOTHIAN
  case object BARNET extends GeoLocation("Barnet", "Underhill Stadium", BigDecimal("51.6464"), BigDecimal("-0.191789")); BARNET
  case object PORT_VALE extends GeoLocation("Port Vale", "Vale Park", BigDecimal("53.05"), BigDecimal("-2.1926")); PORT_VALE
  case object BRADFORD_CITY extends GeoLocation("Bradford City", "Valley Parade", BigDecimal("53.8042"), BigDecimal("-1.75902")); BRADFORD_CITY
  case object WATFORD extends GeoLocation("Watford", "Vicarage Road", BigDecimal("51.6498"), BigDecimal("-0.401569")); WATFORD
  case object HARTLEPOOL_UNITED extends GeoLocation("Hartlepool United", "Victoria Park", BigDecimal("54.6891"), BigDecimal("-1.21274")); HARTLEPOOL_UNITED
  case object ROSS_COUNTY extends GeoLocation("Ross County", "Victoria Park, Dingwall", BigDecimal("57.5959"), BigDecimal("-4.41898")); ROSS_COUNTY
  case object DAGENHAM_AND_REDBRIDGE extends GeoLocation("Dagenham and Redbridge", "Victoria Road", BigDecimal("51.5478"), BigDecimal("0.159739")); DAGENHAM_AND_REDBRIDGE
  case object ASTON_VILLA extends GeoLocation("Aston Villa", "Villa Park", BigDecimal("52.5092"), BigDecimal("-1.88508")); ASTON_VILLA
  case object ENGLAND extends GeoLocation("England", "Wembley", BigDecimal("51.5559"), BigDecimal("-0.279543")); ENGLAND
  case object COLCHESTER_UNITED extends GeoLocation("Colchester United", "Weston Homes Community Stadium", BigDecimal("51.9234"), BigDecimal("0.897861")); COLCHESTER_UNITED
  case object CHELTENHAM_TOWN extends GeoLocation("Cheltenham Town", "Whaddon Road", BigDecimal("51.9062"), BigDecimal("-2.06021")); CHELTENHAM_TOWN
  case object TOTTENHAM_HOTSPUR extends GeoLocation("Tottenham Hotspur", "White Hart Lane", BigDecimal("51.6033"), BigDecimal("-0.065684")); TOTTENHAM_HOTSPUR
  
  def lookFor(team: String): Option[GeoLocation] = {
    val none: Option[Pair[GeoLocation, Double]] = None
    val search = values.values.foldLeft(none) { (closestMatch, geoLocation) =>
      JaroWinklerMetric.compare(team, geoLocation.team.toLowerCase) match {
        case Some(difference) => {
          closestMatch match {
            case Some(closestMatch) => {
              Some(if (difference > closestMatch._2) (geoLocation, difference) else closestMatch)
            }
            case None => Some(geoLocation, difference)
          }
        }
        case None => {
          closestMatch
        }
      }
    }
    search map { case (geoLocation, difference) => geoLocation }
  }
  
  def apply(team: String): Option[GeoLocation] = { val lc = team.toLowerCase; values.get(lc) orElse lookFor(lc) }
}
