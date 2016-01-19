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

import org.squeryl.{PrimitiveTypeMode, Query}

import scala.collection.SortedSet
import scala.language.implicitConversions

/**
 * Squeryl persistence
 */
object SquerylEntryPoint extends PrimitiveTypeMode {

  // implicits for queries
  
  implicit def querySingleOption[E](q: Query[E]): Option[E] = queryList(q).headOption
  
  implicit def queryList[E](q: Query[E]): List[E] = q.iterator.foldLeft(List.empty[E])(_ :+ _)

  implicit def querySortedSet[E](q: Query[E])(implicit ord: Ordering[E]): SortedSet[E] = q.iterator.foldLeft(SortedSet.empty[E])(_ + _)
}
