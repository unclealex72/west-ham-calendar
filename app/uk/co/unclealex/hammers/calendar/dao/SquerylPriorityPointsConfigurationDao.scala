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

import javax.inject.Inject

import pdf.PriorityPointsConfiguration
import uk.co.unclealex.hammers.calendar.dao.CalendarSchema._
import uk.co.unclealex.hammers.calendar.dao.SquerylEntryPoint._
import uk.co.unclealex.hammers.calendar.dates.NowService
import uk.co.unclealex.hammers.calendar.model.PersistedPriorityPointsConfiguration

/**
 * The Squeryl implementation of both GameDao and Transactional.
 * @author alex
 *
 */
class SquerylPriorityPointsConfigurationDao extends PriorityPointsConfigurationDao {

  def get: Option[PriorityPointsConfiguration] = inTransaction {
    priorityPointsConfigurations.allRows.headOption.map(PersistedPriorityPointsConfiguration.toPriorityPointsConfiguration)
  }
}