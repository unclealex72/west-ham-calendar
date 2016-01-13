/**
 * Copyright 2010-2013 Alex Jones
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
package dao

import java.sql.Timestamp
import org.joda.time.DateTime
import org.squeryl.PrimitiveTypeMode
import org.squeryl.dsl.DeOptionizer
import org.squeryl.dsl.NonPrimitiveJdbcMapper
import org.squeryl.dsl.TOptionString
import org.squeryl.dsl.TOptionTimestamp
import org.squeryl.dsl.TString
import org.squeryl.dsl.TTimestamp
import org.squeryl.dsl.TypedExpressionFactory
import model.Competition
import model.Location
import dates.JodaDateTime
import org.squeryl.Query
import scala.collection.SortedSet

/**
 * Squeryl persistence
 */
object SquerylEntryPoint extends PrimitiveTypeMode {

  // implicits for queries
  
  implicit def querySingleOption[E](q: Query[E]): Option[E] = q.singleOption
  
  implicit def queryList[E](q: Query[E]): List[E] = q.iterator.foldLeft(List.empty[E])(_ :+ _)

  implicit def querySortedSet[E](q: Query[E])(implicit ord: Ordering[E]): SortedSet[E] = q.iterator.foldLeft(SortedSet.empty[E])(_ + _)

  // Joda - time support:
  
  implicit val jodaTimeTEF = new NonPrimitiveJdbcMapper[Timestamp, DateTime, TTimestamp](timestampTEF, this) {
    
    /**
     * Here we implement functions fo convert to and from the native JDBC type
     */
    def convertFromJdbc(t: Timestamp) = JodaDateTime(t)
    def convertToJdbc(t: DateTime) = new Timestamp(t.getMillis())
  }

  /**
   * We define this one here to allow working with Option of our new type, this allso 
   * allows the 'nvl' function to work  
   */
  implicit val optionJodaTimeTEF = 
    new TypedExpressionFactory[Option[DateTime], TOptionTimestamp] 
      with DeOptionizer[Timestamp, DateTime, TTimestamp, Option[DateTime], TOptionTimestamp] {

    val deOptionizer = jodaTimeTEF
  }
  
  /**
   * the following are necessary for the AST lifting  
   */
  implicit def jodaTimeToTE(s: DateTime) = jodaTimeTEF.create(s)  

  implicit def optionJodaTimeToTE(s: Option[DateTime]) = optionJodaTimeTEF.create(s)

} 
