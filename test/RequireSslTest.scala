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
/**
 * @author alex
 *
 */
import org.specs2.mutable.Specification
import org.scalamock.specs2.MockFactory
import play.api.mvc.Handler
import play.api.mvc.RequestHeader
import org.specs2.execute.AsResult

class RequireSslTest extends Specification with MockFactory {

  "Development mode" should {
    "not redirect http" in {
      "http://myfavewebsite.com/here?woohoo" within DEV must be equalTo (PassThrough)
    }
    "not redirect https" in {
      "https://myfavewebsite.com/here?woohoo" within DEV must be equalTo (PassThrough)
    }
  }

  "Production mode" should {
    "redirect http" in {
      "http://myfavewebsite.com/here?woohoo" within PROD must be equalTo (Redirect("https://myfavewebsite.com/here?woohoo"))
    }
    "not redirect https" in {
      "https://myfavewebsite.com/here?woohoo" within DEV must be equalTo (PassThrough)
    }
  }

  sealed trait Environment
  case object DEV extends Environment
  case object PROD extends Environment

  sealed trait Result
  case object PassThrough extends Result
  case class Redirect(url: String) extends Result

  implicit class TestCaseBuilder(url: String) {
    def within(env: Environment) = {
      val requestHeader = mock[RequestHeader]
      val sslRouter = new FakeSslRouter
      (requestHeader.uri _) expects () returns (url)
      Global.onRouteRequest(env == PROD, requestHeader, sslRouter)
      sslRouter.result
    }
  }
  class FakeSslRouter extends SslRouter {

    var result: Result = _

    def onAllowed(request: RequestHeader) = {
      result = PassThrough
      None
    }

    def onRedirectRequired(request: RequestHeader, uri: String) = {
      result = Redirect(uri)
      null
    }
  }
}

