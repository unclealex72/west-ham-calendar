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

package json

import argonaut._, Argonaut._
import scala.collection.SortedSet
import scala.collection.SortedMap
import scalaz.Validation

/**
 * A global object used to configure Jackson JSON support.
 * @author alex
 *
 */
object Json {

  def read[T](json: String)(implicit decoder: DecodeJson[T]): Validation[String, T] =
    json.decodeValidation[T]
    
  def apply[A](a: A)(implicit encoder: EncodeJson[A]): String = encoder.encode(a).nospaces
  
  /**
   * JSON serialisation.
   */
  implicit def sortedSetEncodeJson[A](implicit e: EncodeJson[A]): EncodeJson[SortedSet[A]] = 
    EncodeJson((ss: SortedSet[A]) => ss.foldRight(jEmptyArray)((s, json) => e(s) -->>: json))

  /**
   * JSON serialisation.
   */
  implicit def mapEncodeJson[K, V](implicit ek: K => JsonField, ev: EncodeJson[V]): EncodeJson[Map[K, V]] = 
    EncodeJson((sm: Map[K, V]) => sm.foldLeft(jEmptyObject)((json, kv) => (ek(kv._1), ev(kv._2)) ->: json))
}