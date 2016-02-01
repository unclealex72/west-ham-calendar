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
package module

import com.tzavellas.sse.guice.ScalaModule
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import com.google.inject.Guice
import play.api.cache.CacheApi
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext

/**
 * Test that the Guice Calendar Module can be instantiated.
  *
  * @author alex
 *
 */
class CalendarModuleTest extends Specification with Mockito {

    "Application initialisation" should {
      "not error" in {
        class MockModule extends ScalaModule {
          override def configure() = {
            bind[ExecutionContext].toInstance(mock[ExecutionContext])
            bind[CacheApi].toInstance(mock[CacheApi])
            bind[DatabaseConfigProvider].toInstance(mock[DatabaseConfigProvider])
          }
        }
        Guice.createInjector(new MockModule, new CalendarModule)
        success
      }
  }

}