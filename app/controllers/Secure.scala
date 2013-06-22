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
package controllers

import securesocial.core.SecureSocial
import securesocial.core.RequestWithUser
import securesocial.core.Authorization

/**
 * @author alex
 *
 */
trait Secure extends SecureSocial {

  /**
   * Get the email and name of an authorised, logged in user or none if no such user currently exists.
   */
  def emailAndName(implicit request: RequestWithUser[_ <: Any], authorization: Authorization): Option[Pair[String, String]] =
    request.user match {
      case Some(user) => user.email match {
        case Some(email) => if (authorization.isAuthorized(user)) Some(email, user.fullName) else None
        case None => None
      }
      case None => None
    }

}