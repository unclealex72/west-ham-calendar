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

import org.specs2.mutable.Specification

import GeoLocation.ALDERSHOT_TOWN
import GeoLocation.ARSENAL
import GeoLocation.ASTON_VILLA
import GeoLocation.BARNSLEY
import GeoLocation.BIRMINGHAM_CITY
import GeoLocation.BLACKBURN_ROVERS
import GeoLocation.BLACKPOOL
import GeoLocation.BOLTON_WANDERERS
import GeoLocation.BRIGHTON_AND_HOVE_ALBION
import GeoLocation.BRISTOL_CITY
import GeoLocation.BRISTOL_ROVERS
import GeoLocation.BURNLEY
import GeoLocation.CARDIFF_CITY
import GeoLocation.CHELSEA
import GeoLocation.COVENTRY_CITY
import GeoLocation.CRYSTAL_PALACE
import GeoLocation.DERBY_COUNTY
import GeoLocation.DONCASTER_ROVERS
import GeoLocation.EVERTON
import GeoLocation.FULHAM
import GeoLocation.HULL_CITY
import GeoLocation.IPSWICH_TOWN
import GeoLocation.LEEDS_UNITED
import GeoLocation.LEICESTER_CITY
import GeoLocation.LIVERPOOL
import GeoLocation.MANCHESTER_CITY
import GeoLocation.MANCHESTER_UNITED
import GeoLocation.MIDDLESBROUGH
import GeoLocation.MILLWALL
import GeoLocation.NEWCASTLE_UNITED
import GeoLocation.NOTTINGHAM_FOREST
import GeoLocation.PETERBOROUGH_UNITED
import GeoLocation.PLYMOUTH_ARGYLE
import GeoLocation.PORTSMOUTH
import GeoLocation.READING
import GeoLocation.SHEFFIELD_WEDNESDAY
import GeoLocation.SOUTHAMPTON
import GeoLocation.SOUTHEND_UNITED
import GeoLocation.SUNDERLAND
import GeoLocation.TOTTENHAM_HOTSPUR
import GeoLocation.WATFORD
import GeoLocation.WEST_HAM
import GeoLocation.WIGAN_ATHLETIC
/**
 * @author alex
 *
 */
class GeoLocationTest extends Specification {

  // Hand picked tests
  "West Ham" should {
    "be at the Boleyn Ground" in {
      GeoLocation("West Ham") must be equalTo (Some(WEST_HAM))
    }
  }

  "West Ham United" should {
    "be at the Boleyn Ground" in {
      GeoLocation("West Ham United") must be equalTo (Some(WEST_HAM))
    }
  }

  "West Ham Utd" should {
    "be at the Boleyn Ground" in {
      GeoLocation("West Ham Utd") must be equalTo (Some(WEST_HAM))
    }
  }

  "Southampton United" should {
    "be at St. Mary's" in {
      GeoLocation("Southampton United") must be equalTo (Some(SOUTHAMPTON))
    }
  }

  "Southend" should {
    "be at Roots Hall" in {
      GeoLocation("Southend") must be equalTo (Some(SOUTHEND_UNITED))
    }
  }

  // Tests garnered from teams from the West Ham pages

  val teams = List(
    ("Aldershot Town" -> ALDERSHOT_TOWN),
    ("Arsenal" -> ARSENAL),
    ("Aston Villa" -> ASTON_VILLA),
    ("Barnsley" -> BARNSLEY),
    ("Birmingham" -> BIRMINGHAM_CITY),
    ("Blackburn" -> BLACKBURN_ROVERS),
    ("Blackpool" -> BLACKPOOL),
    ("Bolton" -> BOLTON_WANDERERS),
    ("Brighton" -> BRIGHTON_AND_HOVE_ALBION),
    ("Bristol City" -> BRISTOL_CITY),
    ("Bristol Rovers" -> BRISTOL_ROVERS),
    ("Burnley" -> BURNLEY),
    ("Cardiff City" -> CARDIFF_CITY),
    ("Chelsea" -> CHELSEA),
    ("Coventry City" -> COVENTRY_CITY),
    ("Crystal Palace" -> CRYSTAL_PALACE),
    ("Derby County" -> DERBY_COUNTY),
    ("Doncaster" -> DONCASTER_ROVERS),
    ("Everton" -> EVERTON),
    ("Fulham" -> FULHAM),
    ("Hull City" -> HULL_CITY),
    ("Ipswich Town" -> IPSWICH_TOWN),
    ("Leeds United" -> LEEDS_UNITED),
    ("Leicester City" -> LEICESTER_CITY),
    ("Liverpool" -> LIVERPOOL),
    ("Man Utd" -> MANCHESTER_UNITED),
    ("Manchester City" -> MANCHESTER_CITY),
    ("Middlesbrough" -> MIDDLESBROUGH),
    ("Millwall" -> MILLWALL),
    ("Newcastle" -> NEWCASTLE_UNITED),
    ("Nottm Forest" -> NOTTINGHAM_FOREST),
    ("Peterborough" -> PETERBOROUGH_UNITED),
    ("Plymouth" -> PLYMOUTH_ARGYLE),
    ("Portsmouth" -> PORTSMOUTH),
    ("Reading" -> READING),
    ("Sheffield Wed" -> SHEFFIELD_WEDNESDAY),
    ("Southampton" -> SOUTHAMPTON),
    ("Sunderland" -> SUNDERLAND),
    ("Tottenham" -> TOTTENHAM_HOTSPUR),
    ("Watford" -> WATFORD),
    ("Wigan Athletic" -> WIGAN_ATHLETIC))
    
    teams foreach { case (team, expectedGeoLocation) =>
      s"Harvested team $team" should {
        s"be at ${expectedGeoLocation.name}" in {
          GeoLocation(team) should be equalTo(Some(expectedGeoLocation))
        }
      }
    }
}