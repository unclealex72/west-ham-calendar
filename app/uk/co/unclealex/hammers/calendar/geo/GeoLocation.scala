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

import java.io.{PrintWriter, FileWriter, IOException}
import java.net.URL

import location.{LocationServiceImpl, DispatchAsyncHttpClient}

import scala.collection.immutable.SortedMap
import scala.concurrent.Await
import scala.io.Source
import scala.math.BigDecimal

import com.rockymadden.stringmetric.similarity.JaroWinklerMetric
import com.typesafe.scalalogging.slf4j.Logging

import uk.co.unclealex.hammers.calendar.model.Game
import uk.co.unclealex.hammers.calendar.model.Location

import argonaut._, Argonaut._, DecodeResult._

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
   * The Google place_id for this location.
   */
  val placeId: String,

  /**
   * A list of alternative team names to search against.
   */
  val alternativeNames: String*) extends GeoLocation.Value

object GeoLocation extends GeoLocationLike {

  case object ABERDEEN extends GeoLocation("Aberdeen", "Pittodrie", "ChIJtWz9OhMOhEgR5fclLEJJdqA"); ABERDEEN
  case object ACCRINGTON_STANLEY extends GeoLocation("Accrington Stanley", "Crown Ground", "ChIJfdvsWiKZe0gRYjBQvlMNkes"); ACCRINGTON_STANLEY
  case object AFC_WIMBLEDON extends GeoLocation("AFC Wimbledon", "Kingsmeadow", "ChIJmwqV2GEJdkgRxBVQjCfnpRw"); AFC_WIMBLEDON
  case object AIRDRIE_UNITED extends GeoLocation("Airdrie United", "New Broomfield", "ChIJVYW9y55GiEgR6o5ArsCtkys"); AIRDRIE_UNITED
  case object ALBION_ROVERS extends GeoLocation("Albion Rovers", "Cliftonhill", "ChIJrWmZ0udriEgRvlO8bTKpQmQ"); ALBION_ROVERS
  case object ALDERSHOT_TOWN extends GeoLocation("Aldershot Town", "The Recreation Ground", "ChIJIaSiIbIsdEgRWt1ZQWa5OHM"); ALDERSHOT_TOWN
  case object ALLOA_ATHLETIC extends GeoLocation("Alloa Athletic", "The Recreation Park", "ChIJk1EtyHZ-iEgRb1Tj8gqYEwY"); ALLOA_ATHLETIC
  case object ANNAN_ATHLETIC extends GeoLocation("Annan Athletic", "Galabank", "ChIJPSZRBj06fUgR6dKsSvSDejQ"); ANNAN_ATHLETIC
  case object ARBROATH extends GeoLocation("Arbroath", "Gayfield Park", "ChIJgTcGQyGNhkgRDdlt4ZBE4hQ"); ARBROATH
  case object ARSENAL extends GeoLocation("Arsenal", "The Emirates Stadium", "ChIJO14pRXYbdkgRkM-CgzxxADY"); ARSENAL
  case object ASTON_VILLA extends GeoLocation("Aston Villa", "Villa Park", "ChIJgW_5qKm8cEgR1XUj-wo2Xqw"); ASTON_VILLA
  case object AYR_UNITED extends GeoLocation("Ayr United", "Somerset Park", "ChIJu-eGPSLUiUgRTV3eTZF_74Q"); AYR_UNITED
  case object BARNET extends GeoLocation("Barnet", "Underhill Stadium", "ChIJ7eHK1YAXdkgRh_q1_ZaxNAQ"); BARNET
  case object BARNSLEY extends GeoLocation("Barnsley", "Oakwell", "ChIJER7MX8x6eUgR8cDwhJzdxAg"); BARNSLEY
  case object BERWICK_RANGERS extends GeoLocation("Berwick Rangers", "Shielfield Park", "ChIJ7YevMRlGh0gRUJH-t3qC3FI"); BERWICK_RANGERS
  case object BIRMINGHAM_CITY extends GeoLocation("Birmingham City", "St Andrews", "ChIJk6KUF3i7cEgRwlOYkb6wzG8"); BIRMINGHAM_CITY
  case object BLACKBURN_ROVERS extends GeoLocation("Blackburn Rovers", "Ewood Park", "ChIJeUqHqOafe0gRVm2QbMgZn1o"); BLACKBURN_ROVERS
  case object BLACKPOOL extends GeoLocation("Blackpool", "Bloomfield Road", "ChIJ24jjvAxEe0gRJFNCO27S7Y4"); BLACKPOOL
  case object BOLTON_WANDERERS extends GeoLocation("Bolton Wanderers", "The Reebok Stadium", "ChIJDwNws6oJe0gR6668ITr4nBI"); BOLTON_WANDERERS
  case object BOURNEMOUTH extends GeoLocation("Bournemouth", "Seward Stadium", "ChIJ95nLCkefc0gRJw-vyxosN5M"); BOURNEMOUTH
  case object BRADFORD_CITY extends GeoLocation("Bradford City", "Valley Parade", "ChIJZ5NWDlPhe0gRZRacOFajDUE"); BRADFORD_CITY
  case object BRECHIN_CITY extends GeoLocation("Brechin City", "Glebe Park", "ChIJ7dbY1emFhkgRdh3qppwnR5M"); BRECHIN_CITY
  case object BRENTFORD extends GeoLocation("Brentford", "Griffin Park", "ChIJX8Ka18YNdkgRYeoJ-AtdL9w"); BRENTFORD
  case object BRIGHTON_AND_HOVE_ALBION extends GeoLocation("Brighton and Hove Albion", "Amex Stadium", "ChIJZ-25JbGIdUgRrEsPjKDwyZg"); BRIGHTON_AND_HOVE_ALBION
  case object BRISTOL_CITY extends GeoLocation("Bristol City", "Ashton Gate", "ChIJgfBJR0qMcUgRWqd0jrmecWo"); BRISTOL_CITY
  case object BRISTOL_ROVERS extends GeoLocation("Bristol Rovers", "The Memorial Stadium", "ChIJLefkgB2OcUgRdTmvnX8sAwI"); BRISTOL_ROVERS
  case object BURNLEY extends GeoLocation("Burnley", "Turf Moor", "ChIJ9cwq0tuWe0gRnQ-s4XzB6Kw"); BURNLEY
  case object BURTON_ALBION extends GeoLocation("Burton Albion", "The Pirelli Stadium", "ChIJBfwNDfUCekgRVzzBsCnPq9U"); BURTON_ALBION
  case object BURY extends GeoLocation("Bury", "Gigg Lane", "ChIJxb1uEyGle0gRG_bye3o-mOs"); BURY
  case object CARDIFF_CITY extends GeoLocation("Cardiff City", "Cardiff City Stadium", "ChIJU_8JlrQEbkgRnQdnk4XgOZc"); CARDIFF_CITY
  case object CARLISLE_UNITED extends GeoLocation("Carlisle United", "Brunton Park", "ChIJXea8hEcafUgRCEffBdfHTf4"); CARLISLE_UNITED
  case object CELTIC extends GeoLocation("Celtic", "Celtic Park", "ChIJD9s2ZE9BiEgRJB1wSch4Fw8"); CELTIC
  case object CHARLTON_ATHLETIC extends GeoLocation("Charlton Athletic", "The Valley", "ChIJBX1Pz0So2EcR2znkmqskdWU"); CHARLTON_ATHLETIC
  case object CHELSEA extends GeoLocation("Chelsea", "Stamford Bridge", "ChIJsTWPS4YPdkgRHtLvawx5Ed4"); CHELSEA
  case object CHELTENHAM_TOWN extends GeoLocation("Cheltenham Town", "Whaddon Road", "ChIJE4HxsO0bcUgRloqOV2EKvic"); CHELTENHAM_TOWN
  case object CHESTERFIELD extends GeoLocation("Chesterfield", "B2net Stadium", "ChIJFSBVcTuFeUgRl8Y7SrZjTQE"); CHESTERFIELD
  case object CLYDE extends GeoLocation("Clyde", "The Broadwood Stadium", "ChIJmdeOIZFoiEgRNzDVCJiQ1HI"); CLYDE
  case object COEDENBEATH extends GeoLocation("Coedenbeath", "Central Park", "ChIJCSwki17Mh0gRdqmHvjyRRpQ"); COEDENBEATH
  case object COLCHESTER_UNITED extends GeoLocation("Colchester United", "The Weston Homes Community Stadium", "ChIJj8RQtiQE2UcRWd844mGsXQc"); COLCHESTER_UNITED
  case object COVENTRY_CITY extends GeoLocation("Coventry City", "Ricoh Arena", "ChIJOc4z-RFMd0gRWB8d_K5FwbU"); COVENTRY_CITY
  case object CRAWLEY_TOWN extends GeoLocation("Crawley Town", "Broadfield Stadium", "ChIJj4ePQgbudUgRaBijcXRYvWg"); CRAWLEY_TOWN
  case object CREWE_ALEXANDRA extends GeoLocation("Crewe Alexandra", "Gresty Road", "ChIJhXM7PF5fekgRD__SieniJig"); CREWE_ALEXANDRA
  case object CRYSTAL_PALACE extends GeoLocation("Crystal Palace", "Selhurst Park", "ChIJcyVGWSkBdkgRUW-XVSWc9fA"); CRYSTAL_PALACE
  case object DAGENHAM_AND_REDBRIDGE extends GeoLocation("Dagenham and Redbridge", "Victoria Road", "ChIJ8ROgQRil2EcRvIi7XALVxo0"); DAGENHAM_AND_REDBRIDGE
  case object DERBY_COUNTY extends GeoLocation("Derby County", "Pride Park", "ChIJ_wO4dKnxeUgRhqFAi7SzloU"); DERBY_COUNTY
  case object DONCASTER_ROVERS extends GeoLocation("Doncaster Rovers", "Keepmoat Stadium", "ChIJ0ybYWYEOeUgRY_joF07vEeI"); DONCASTER_ROVERS
  case object DUMBARTON extends GeoLocation("Dumbarton", "Dumbarton Football Stadium", "ChIJv7ivMGBNiEgRbbCc5Sr89iE"); DUMBARTON
  case object DUNDEE extends GeoLocation("Dundee", "Dens Park", "ChIJw16sX-dchkgRUv052aMe2fs"); DUNDEE
  case object DUNDEE_UNITED extends GeoLocation("Dundee United", "Tannadice Park", "ChIJ7ws_3OZchkgR9ZIQQzYupSI"); DUNDEE_UNITED
  case object DUNFERMLINE extends GeoLocation("Dunfermline", "East End Park", "ChIJexBt0xrSh0gRa294ds_EGrI"); DUNFERMLINE
  case object EAST_FIFE extends GeoLocation("East Fife", "Bayview Stadium", "ChIJi5Ukxj9NhkgR7Js8nj7tlas"); EAST_FIFE
  case object EAST_STIRLINGSHIRE extends GeoLocation("East Stirlingshire", "Ochilview Park", "ChIJGf9vD8piiEgRUhwnfgmV4jg"); EAST_STIRLINGSHIRE
  case object ELGIN_CITY extends GeoLocation("Elgin City", "Borough Briggs", "ChIJ-bqPYKkLhUgRMLYLgnB7Yj0"); ELGIN_CITY
  case object ENGLAND extends GeoLocation("England", "Wembley", "ChIJbYd61YERdkgRl-DoXRifT6Y"); ENGLAND
  case object EVERTON extends GeoLocation("Everton", "Goodison Park", "ChIJ4VrZFmIhe0gRWXOEyaeteDc"); EVERTON
  case object EXETER_CITY extends GeoLocation("Exeter City", "St James Park, Exeter", "ChIJbx7aMWukbUgRXmb37RF81A0"); EXETER_CITY
  case object FALKIRK extends GeoLocation("Falkirk", "Falkirk Stadium", "ChIJhV_YoIV5iEgRlLRQ0new3vY"); FALKIRK
  case object FLEETWOOD_TOWN extends GeoLocation("Fleetwood Town", "Highbury Stadium", "ChIJt3l4eV9ce0gRM3ex-uGHBog"); FLEETWOOD_TOWN
  case object FORFAR_ATHLETIC extends GeoLocation("Forfar Athletic", "Station Park", "ChIJIRRUEJpjhkgRqYJeINrDuzs"); FORFAR_ATHLETIC
  case object FULHAM extends GeoLocation("Fulham", "Craven Cottage", "ChIJV4FIpKcPdkgRBS2UjWR_N6A"); FULHAM
  case object GILLINGHAM extends GeoLocation("Gillingham", "Priestfield Stadium", "ChIJ7X7Q937N2EcRYrQL4kQktlc"); GILLINGHAM
  case object GREENOCK_MORTON extends GeoLocation("Greenock Morton", "Cappielow Park", "ChIJ2cYOfBmuiUgRTnlDwTBB5G8"); GREENOCK_MORTON
  case object HAMILTON_ACADEMICAL extends GeoLocation("Hamilton Academical", "New Douglas Park", "ChIJc6-H_MpqiEgRp5y4oC1os8E"); HAMILTON_ACADEMICAL
  case object HARTLEPOOL_UNITED extends GeoLocation("Hartlepool United", "Victoria Park", "ChIJAxPZxYfzfkgRaz6oDB-fwww"); HARTLEPOOL_UNITED
  case object HEART_OF_MIDLOTHIAN extends GeoLocation("Heart of Midlothian", "Tynecastle", "ChIJk5UplqrHh0gRBQPpDnhcTp0"); HEART_OF_MIDLOTHIAN
  case object HEREFORD_UNITED extends GeoLocation("Hereford United", "Edgar Street", "ChIJKS6-QTpKcEgRL5ds_6ZzRR0"); HEREFORD_UNITED
  case object HIBERNIAN extends GeoLocation("Hibernian", "Easter Road", "ChIJ_6sCtHK4h0gR2ntPwZuNPLU"); HIBERNIAN
  case object HUDDERSFIELD_TOWN extends GeoLocation("Huddersfield Town", "The Galpharm Stadium", "ChIJCXkP2Bbce0gRa9suClauToY"); HUDDERSFIELD_TOWN
  case object HULL_CITY extends GeoLocation("Hull City", "The KC Stadium", "ChIJi0YDOtC_eEgR_-xrXXd8RtI"); HULL_CITY
  case object INVERNESS_CALEDONIAN_THISTLE extends GeoLocation("Inverness Caledonian Thistle", "The Caledonian Stadium", "ChIJ-RIXxrl2j0gRAGxjhMgmIFY"); INVERNESS_CALEDONIAN_THISTLE
  case object IPSWICH_TOWN extends GeoLocation("Ipswich Town", "Portman Road", "ChIJBZPEGzOg2UcRmDy6wPaAspc"); IPSWICH_TOWN
  case object KILMARNOCK extends GeoLocation("Kilmarnock", "Rugby Park", "ChIJqUIv274yiEgRGuFjsuWczMA"); KILMARNOCK
  case object KINGSTONIAN extends GeoLocation("Kingstonian", "Kingsmeadow", "ChIJazzNYd8LdkgRJq5YEM5cMlM"); KINGSTONIAN
  case object LEEDS_UNITED extends GeoLocation("Leeds United", "Elland Road", "ChIJk61OZ4xeeUgR3FJeO45LzB4"); LEEDS_UNITED
  case object LEICESTER_CITY extends GeoLocation("Leicester City", "The King Power Stadium", "ChIJyytWPddgd0gRqpkjDhfgic0"); LEICESTER_CITY
  case object LEYTON_ORIENT extends GeoLocation("Leyton Orient", "Brisbane Road", "ChIJpZKz25wddkgRUd04Gu8OVvg"); LEYTON_ORIENT
  case object LIVERPOOL extends GeoLocation("Liverpool", "Anfield", "ChIJHTprz3ohe0gRwzMN2ouj9nY"); LIVERPOOL
  case object LIVINGSTON extends GeoLocation("Livingston", "Almondvale Stadium", "ChIJn8XgIFXZh0gRivrvqNy6b3A"); LIVINGSTON
  case object MACCLESFIELD_TOWN extends GeoLocation("Macclesfield Town", "Moss Rose", "ChIJ42CAuydJekgRLFNZjWP7wso"); MACCLESFIELD_TOWN
  case object MANCHESTER_CITY extends GeoLocation("Manchester City", "Etihad Stadium", "ChIJ4wqVzA2xe0gRaod65syNmlQ"); MANCHESTER_CITY
  case object MANCHESTER_UNITED extends GeoLocation("Manchester United", "Old Trafford", "ChIJaX_k53Kue0gRScxJi_CVlMY"); MANCHESTER_UNITED
  case object MIDDLESBROUGH extends GeoLocation("Middlesbrough", "Riverside", "ChIJMw1u5KDtfkgRN5eDTZX1Rc8"); MIDDLESBROUGH
  case object MILLWALL extends GeoLocation("Millwall", "The Den", "ChIJh-eG9hwDdkgRAn9dPECFX44"); MILLWALL
  case object MK_DONS extends GeoLocation("MK Dons", "StadiumMK", "ChIJN9TM4y9VdkgRdc9HFtqrPDI"); MK_DONS
  case object MONTROSE extends GeoLocation("Montrose", "Links Park", "ChIJ32mwHaGQhkgRONz3NfvN_1Q"); MONTROSE
  case object MORECAMBE extends GeoLocation("Morecambe", "The Globe Arena", "ChIJbWMHpwuefEgR7T0n16OvfeA"); MORECAMBE
  case object MOTHERWELL extends GeoLocation("Motherwell", "Fir Park", "ChIJO56LoKwUiEgR4XkQ6R2o800"); MOTHERWELL
  case object NEWCASTLE_UNITED extends GeoLocation("Newcastle United", "St James Park", "ChIJG-cE3DR3fkgRoCrZpWmB_is"); NEWCASTLE_UNITED
  case object NORTHAMPTON_TOWN extends GeoLocation("Northampton Town", "Sixfields Stadium", "ChIJE40YH1IOd0gRgqDW8MophXM"); NORTHAMPTON_TOWN
  case object NORWICH_CITY extends GeoLocation("Norwich City", "Carrow Road", "ChIJd3_4z5Lj2UcRWpwiUYL4s6w"); NORWICH_CITY
  case object NOTTINGHAM_FOREST extends GeoLocation("Nottingham Forest", "The City Ground", "ChIJsTQXGcTDeUgRuqpe_-qTBx8"); NOTTINGHAM_FOREST
  case object NOTTS_COUNTY extends GeoLocation("Notts County", "Meadow Lane", "ChIJI3kaYcXDeUgRP_lH0cA4les"); NOTTS_COUNTY
  case object OLDHAM_ATHLETIC extends GeoLocation("Oldham Athletic", "Boundary Park", "ChIJn1nnLMW5e0gRqhGM3EI2bnU"); OLDHAM_ATHLETIC
  case object OXFORD_UNITED extends GeoLocation("Oxford United", "Kassam Stadium", "ChIJYyVDhfzAdkgRzmwpomshMow"); OXFORD_UNITED
  case object PARTICK_THISTLE extends GeoLocation("Partick Thistle", "Firhill", "ChIJB3PwKjhEiEgR4eaWr5Wexzo"); PARTICK_THISTLE
  case object PETERBOROUGH_UNITED extends GeoLocation("Peterborough United", "London Road", "ChIJlT2QQVLwd0gRRYRy9_SleAU"); PETERBOROUGH_UNITED
  case object PETERHEAD extends GeoLocation("Peterhead", "Balmoor", "ChIJ_w7l6ZuGg0gRLn2Jy5D86MU"); PETERHEAD
  case object PLYMOUTH_ARGYLE extends GeoLocation("Plymouth Argyle", "Home Park", "ChIJp6C9ci-TbEgRjfkEBYO00Tw"); PLYMOUTH_ARGYLE
  case object PORT_VALE extends GeoLocation("Port Vale", "Vale Park", "ChIJoYOnm-1CekgRDuqt-BBs8oc"); PORT_VALE
  case object PORTSMOUTH extends GeoLocation("Portsmouth", "Fratton Park", "ChIJe_xpK6tddEgRc4fWnecn2sI"); PORTSMOUTH
  case object PRESTON_NORTH_END extends GeoLocation("Preston North End", "Deepdale", "ChIJuZwrfxVye0gRYKZ-IbBzA4w"); PRESTON_NORTH_END
  case object QUEEN_OF_THE_SOUTH extends GeoLocation("Queen of the South", "Palmerston Park", "ChIJPyrMYRDKYkgRn0ciQOGQt-E"); QUEEN_OF_THE_SOUTH
  case object QUEENS_PARK extends GeoLocation("Queen's Park", "Hampden Park", "ChIJKdz_neZGiEgR1uJIeGgNVnI"); QUEENS_PARK
  case object QUEENS_PARK_RANGERS extends GeoLocation("Queens Park Rangers", "Loftus Road", "ChIJqTg3gNMPdkgRQkkixzZU8Tk"); QUEENS_PARK_RANGERS
  case object RAITH_ROVERS extends GeoLocation("Raith Rovers", "Stark's Park", "ChIJe3Z1v5W1h0gROsUR8iYCo_0"); RAITH_ROVERS
  case object RANGERS extends GeoLocation("Rangers", "Ibrox", "ChIJoQ6BJ3NGiEgRSq_olgANDdU"); RANGERS
  case object READING extends GeoLocation("Reading", "The Madjeski Stadium", "ChIJG7PBnIOcdkgR93cyfkEOCMM"); READING
  case object ROCHDALE extends GeoLocation("Rochdale", "Spotland Stadium", "ChIJBSxB0eS7e0gRVvmV35pg6OM"); ROCHDALE
  case object ROSS_COUNTY extends GeoLocation("Ross County", "Victoria Park, Dingwall", "ChIJp2o_1Eagj0gR3aMBZJPDDuE"); ROSS_COUNTY
  case object ROTHERHAM_UNITED extends GeoLocation("Rotherham United", "New York Stadiumn", "ChIJGx8RHfh2eUgRbY7XNfyaPis"); ROTHERHAM_UNITED
  case object SCOTLAND extends GeoLocation("Scotland", "Hampden Park", "ChIJKdz_neZGiEgRacxUkaONGDE"); SCOTLAND
  case object SCUNTHORPE_UNITED extends GeoLocation("Scunthorpe United", "Glanford Park", "ChIJdSn6lZbkeEgR9zM5O1q-uWs"); SCUNTHORPE_UNITED
  case object SHEFFIELD_UNITED extends GeoLocation("Sheffield United", "Bramall Lane", "ChIJuQ7ciYiCeUgRfb0ToqHUnz4"); SHEFFIELD_UNITED
  case object SHEFFIELD_WEDNESDAY extends GeoLocation("Sheffield Wednesday", "Hillsborough", "ChIJf_CJ-Oh4eUgR5pViDCtDaEw"); SHEFFIELD_WEDNESDAY
  case object SHREWSBURY_TOWN extends GeoLocation("Shrewsbury Town", "New Meadow", "ChIJp1ieEjmeekgRPva-3aTNpYw"); SHREWSBURY_TOWN
  case object SOUTHAMPTON extends GeoLocation("Southampton", "St Mary's Stadium", "ChIJx8YDpU5xdEgRYvEKaVbmOTs"); SOUTHAMPTON
  case object SOUTHEND_UNITED extends GeoLocation("Southend United", "Roots Hall", "ChIJbZer2q7Z2EcRvzo0nZnVUQU"); SOUTHEND_UNITED
  case object ST_JOHNSTONE extends GeoLocation("St Johnstone", "McDiarmid Park", "ChIJzX1C1LMkhkgRpwNSK5Z34F8"); ST_JOHNSTONE
  case object ST_MIRREN extends GeoLocation("St Mirren", "St Mirren Park", "ChIJhTDmDTZJiEgR0qOXYmPnER4"); ST_MIRREN
  case object STENHOUSEMUIR extends GeoLocation("Stenhousemuir", "Ochilview Park", "ChIJTTPIgbx7iEgRX0Mv7B8u7wo"); STENHOUSEMUIR
  case object STEVENAGE_BOROUGH extends GeoLocation("Stevenage Borough", "The Lamex Stadium", "ChIJaxRt3d8vdkgRzqAJi3tfwCI"); STEVENAGE_BOROUGH
  case object STIRLING_ALBION extends GeoLocation("Stirling Albion", "Forthbank Stadium", "ChIJGf9vD8piiEgRUhwnfgmV4jg"); STIRLING_ALBION
  case object STOKE_CITY extends GeoLocation("Stoke City", "The Britannia Stadium", "ChIJb4qUglRoekgRR1JdSfXh4Cs"); STOKE_CITY
  case object STRANRAER extends GeoLocation("Stranraer", "Stair Park", "ChIJT6WrGM07YkgRcBXhrWE62H4"); STRANRAER
  case object SUNDERLAND extends GeoLocation("Sunderland", "The Stadium of Light", "ChIJVVVVZXxmfkgRwsBj9Lqd5k4"); SUNDERLAND
  case object SWANSEA_CITY extends GeoLocation("Swansea City", "Liberty Stadium", "ChIJhbU1kmj1bkgRkB1I94UJXh4"); SWANSEA_CITY
  case object SWINDON_TOWN extends GeoLocation("Swindon Town", "The County Ground", "ChIJKb6Oq0pEcUgRo9Y-xc68ZM8"); SWINDON_TOWN
  case object TORQUAY_UNITED extends GeoLocation("Torquay United", "Plainmoor", "ChIJfzoQUskPbUgRS0VlhUpU0cs"); TORQUAY_UNITED
  case object TOTTENHAM_HOTSPUR extends GeoLocation("Tottenham Hotspur", "White Hart Lane", "ChIJ17zD354edkgR0iyBktlfh4Q"); TOTTENHAM_HOTSPUR
  case object TRANMERE_ROVERS extends GeoLocation("Tranmere Rovers", "Prenton Park", "ChIJqbJMS20ne0gROSuBlrnI32o"); TRANMERE_ROVERS
  case object WALES extends GeoLocation("Wales", "The Millennium Stadium", "ChIJdRIdItMcbkgROMygC44OJ3E"); WALES
  case object WALSALL extends GeoLocation("Walsall", "Bescot Stadium", "ChIJ8UKUCQyicEgRsrIABoP_Bqk"); WALSALL
  case object WATFORD extends GeoLocation("Watford", "Vicarage Road", "ChIJK85fQt1qdkgRYzX5AxgaePk"); WATFORD
  case object WEST_BROMWICH_ALBION extends GeoLocation("West Bromwich Albion", "The Hawthorns", "ChIJH3kSWkm9cEgR7xDiT10vVCU", "WBA"); WEST_BROMWICH_ALBION
  case object WEST_HAM extends GeoLocation("West Ham", "The Boleyn Ground", "ChIJ8-luJ8qn2EcRz6EB2AHQdQg"); WEST_HAM
  case object WIGAN_ATHLETIC extends GeoLocation("Wigan Athletic", "DW Stadium", "ChIJO52GEIUPe0gRFsVW3H4CFT8"); WIGAN_ATHLETIC
  case object WOLVERHAMPTON_WANDERERS extends GeoLocation("Wolverhampton Wanderers", "Molineux", "ChIJmyKap4WbcEgRRDifnXygVFM", "Wolves"); WOLVERHAMPTON_WANDERERS
  case object WYCOMBE_WANDERERS extends GeoLocation("Wycombe Wanderers", "Adams Park", "ChIJi1k781CKdkgR3Ii21vnJNs8"); WYCOMBE_WANDERERS
  case object YEOVIL_TOWN extends GeoLocation("Yeovil Town", "Huish Park", "ChIJIVEfefJqckgR3xhdhIehZZc"); YEOVIL_TOWN
  case object YORK_CITY extends GeoLocation("York City", "Bootham Crescent", "ChIJ15UZdwoxeUgRLFcqfriUEWU"); YORK_CITY

  def lookFor(team: String): Option[GeoLocation] = {
    val lowerCaseTeam = team.toLowerCase
    val largestDifference = { (geolocation: GeoLocation) =>
      val differences = geolocation.team :: geolocation.alternativeNames.toList flatMap {
        (teamName: String) => JaroWinklerMetric.compare(lowerCaseTeam, teamName.toLowerCase)
      }
      differences match {
        case Nil => None
        case _ => Some(differences.max)
      }
    }
    val largestDifferences: GeoLocation => Traversable[Pair[GeoLocation, Double]] = { (geoLocation: GeoLocation) =>
      largestDifference(geoLocation) map (difference => geoLocation -> difference)
    }
    values.values.flatMap(largestDifferences).toList match {
      case Nil => None
      case largestDifferences => Some(largestDifferences.maxBy(_._2)._1)
    }
  }

  /**
   * Get a geographic location from a team.
   */
  def apply(team: String): Option[GeoLocation] = { val lc = team.toLowerCase; values.get(lc) orElse lookFor(lc) }

  /**
   * Get a geographic location for a game.
   */
  def apply(game: Game): Option[GeoLocation] = game.location match {
    case Location.HOME => Some(WEST_HAM)
    case Location.AWAY => apply(game.opponents)
  }

  /**
   * Json Serialisation
   */
  implicit val GeoLocationCodecJson: EncodeJson[GeoLocation] =
    jencode2L((geo: GeoLocation) => (geo.name, geo.placeId))("name", "placeId")
}