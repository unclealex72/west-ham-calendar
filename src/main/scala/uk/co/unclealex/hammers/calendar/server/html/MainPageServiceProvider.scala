/**
 * Copyright 2010-2012 Alex Jones
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

package uk.co.unclealex.hammers.calendar.server.html;

import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.util.Iterator
import javax.annotation.PostConstruct
import org.htmlcleaner.TagNode
import org.mozilla.javascript.Context
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.Scriptable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Provider
import com.typesafe.scalalogging.slf4j.Logging
import scala.collection.JavaConversions._
import uk.co.unclealex.hammers.calendar.server.html.TagNodeImplicits._

/**
 * The default implementation of {@link MainPageService}.
 *
 * @author alex
 *
 */
class MainPageServiceProvider(
  /**
   * The URI for the main web page.
   */
  mainPageUri: URI,
  /**
   * The {@link HtmlPageLoader} used to load the main page.
   */
  htmlPageLoader: HtmlPageLoader) extends Provider[MainPageService] with Logging {

  override def get: MainPageService = {
    val mainPage = htmlPageLoader.loadPage(mainPageUri.toURL)
    val filter = new TagNodeFilter(tagNode => "script" == tagNode.getName)
    val links = filter.list(mainPage).toStream.flatMap(searchForLinks).headOption
    links match {
      case Some((foundTicketsUri, foundFixturesUri)) => new MainPageService() {
        val ticketsUri = foundTicketsUri
        val fixturesUri = foundFixturesUri
      }
      case _ => throw new IOException("Cannot find both the fixtures and tickets list from the main page " + mainPageUri)
    }
  }

  /**
   * Search for and populate the ticket and fixtures link within a javascript
   * script element.
   *
   * @param scriptNode
   *          The {@link TagNode} of the javascript element.
   * @return True if the links were found and populated, false otherwise.
   */
  protected def searchForLinks: TagNode => Traversable[Pair[URI, URI]] = { scriptNode =>
    var fixturesUri: Option[URI] = None
    var ticketsUri: Option[URI] = None

    /**
     * Search for links in a javascript array.
     * @param array The array to search.
     * @param scope The original scope.
     * @return True if the links are found, false otherwise.
     */
    def searchForLinksInArray(array: NativeArray, scope: Scriptable): Option[Pair[URI, URI]] = {
      val searcher = new ObjectSearcher() {
        @Override
        def search(obj: Any): Option[Pair[URI, URI]] = {
          obj match {
            case nativeArray: NativeArray => searchForLinksInArray(nativeArray, scope)
            case s: Scriptable => {
              if (s.has("name", scope) && s.has("uri", scope)) {
                val relativePath = s.get("uri", scope).toString
                val uri = mainPageUri.resolve(relativePath)
                val name = s.get("name", scope)
                name match {
                  case "Fixtures &amp; Results" => {
                    logger info s"Found fixtures link: $uri"
                    fixturesUri = Some(uri)
                  }
                  case "Ticket News" => {
                    logger info s"Found tickets link: $uri"
                    ticketsUri = Some(uri)
                  }
                  case _ => // Ignore.
                }
                if (fixturesUri.isDefined && ticketsUri.isDefined) {
                  Some((ticketsUri.get, fixturesUri.get))
                } else {
                  None
                }
              } else {
                None
              }
            }
            case _ => None
          }
        }

        def get(id: String) = array.get(id)
        def get(id: Int) = array.get(id)
      }
      return searcher.searchIds(array.getIds)
    }

    // Search recursively through javascript variables.
    val cx = Context.enter()
    try {
      val scope = cx.initStandardObjects()
      val script = scriptNode.text.replace('\n', ' ')
      try {
        cx.evaluateString(scope, script, "<cmd>", 1, null)
      } catch {
        // Silently ignore any exceptions
        case e: Exception => Unit
      }
      val searcher = new ObjectSearcher() {
        def search(obj: Any) = {
          obj match {
            case nativeArray: NativeArray => searchForLinksInArray(nativeArray, scope)
            case _ => None
          }
        }

        override def get(id: String) = scope.get(id, scope)
        override def get(id: Int) = scope.get(id, scope)
      }
      searcher.searchIds(scope.getIds())
    } finally {
      Context.exit();
    }
  }

  /**
   * An {@link ObjectSearcher} is an abstract class that can be used to search
   * for links in a javascript array.
   *
   * @author alex
   *
   */
  abstract class ObjectSearcher {

    /**
     * Search a javascript variable context for links.
     *
     * @param ids
     *          The ids of each javascript variable.
     * @return True if a link is found, false otherwise.
     */
    def searchIds(ids: Array[_]): Option[Pair[URI, URI]] = {
      val javascriptVariables = ids.toStream.map {
        case id: Number => get(id.intValue)
        case id: Any => get(id.toString)
      }
      val searchIdsOrObject: Any => Traversable[Pair[URI, URI]] = { obj =>
        obj match {
          case arr: Array[_] => searchIds(arr)
          case obj => search(obj)
        }
      }
      javascriptVariables.flatMap(searchIdsOrObject).headOption
    }

    /**
     * Search an instance of a javascript variable.
     *
     * @param obj
     *          The javascript variable to search.
     * @return True if whatever was being searched for was found, false
     *         otherwise.
     */
    def search(obj: Any): Option[Pair[URI, URI]]

    /**
     * Get a javascript sub-variable by its interger id.
     *
     * @param id
     *          The id of the sub-variable.
     * @return The value of the sub-variable.
     */
    def get(id: Int): Any

    /**
     * Get a javascript sub-variable by its string id.
     *
     * @param id
     *          The id of the sub-variable.
     * @return The value of the sub-variable.
     */
    def get(id: String): Any
  }
}
