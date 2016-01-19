/**
 * Copyright 2013 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
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
 * @author unclealex72
 *
 */

/**
 * @author alex
 *
 */
import com.google.inject.Guice
import com.typesafe.scalalogging.slf4j.StrictLogging
import module.CalendarModule
import org.squeryl.{Session, SessionFactory}
import org.squeryl.adapters.{H2Adapter, PostgreSqlAdapter}
import org.squeryl.internals.DatabaseAdapter
import play.api.{Application, GlobalSettings}
import play.api.db.DB
import play.api.mvc.Results

/**
 * The Play Framework global entry point.
 */
object Global extends GlobalSettings with StrictLogging with Results {

  override def onStart(app: Application) {
    logger info "Setting up database access."
    // Set up Squeryl database access
    SessionFactory.concreteFactory = app.configuration.getString("db.default.driver") match {
      case Some("org.h2.Driver") => Some(() => getSession(new H2Adapter, app))
      case Some("org.postgresql.Driver") => Some(() => getSession(new PostgreSqlAdapter, app))
      case _ => sys.error("Database driver must be either org.h2.Driver or org.postgresql.Driver")
    }
    // Schedule the update job
    logger info "Setting up calendar updates."
  }

  def getSession(adapter: DatabaseAdapter, app: Application) = Session.create(DB.getConnection()(app), adapter)
}
