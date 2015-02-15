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
package uk.co.unclealex.hammers.calendar.dao

import org.squeryl.Schema
import uk.co.unclealex.hammers.calendar.dao.SquerylEntryPoint._
import uk.co.unclealex.hammers.calendar.model.{Game, PersistedClient, PersistedPriorityPointsConfiguration}

/**
 * The Squeryl schema definition for this application.
 * @author alex
 *
 */
object CalendarSchema extends Schema {

  val games = table[Game]("game")

  /**
   * Column constraints
   */
  on(games)(g => declare(
    g.id is (autoIncremented),
    g.gameKeyComposite is (unique)))

  // Priority point form configuration

  val priorityPointsConfigurations = table[PersistedPriorityPointsConfiguration]("prioritypointsconfiguration")
  on(priorityPointsConfigurations)(p => declare(
    p.id is (primaryKey, autoIncremented("ppc_seq"))))

  val clients = table[PersistedClient]("client")
  on(clients)(c => declare(
    c.id is (primaryKey, autoIncremented("c_seq")),
    c.priorityPointsConfigurationId is (indexed("idxPpcIdIdx"))))

  val priorityPointsConfigurationToClients =
    oneToManyRelation(priorityPointsConfigurations, clients).via((p, c) => p.id === c.priorityPointsConfigurationId)

}