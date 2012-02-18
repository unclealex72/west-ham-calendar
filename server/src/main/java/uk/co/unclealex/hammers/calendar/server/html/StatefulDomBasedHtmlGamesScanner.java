/**
 * Copyright 2011 Alex Jones
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

package uk.co.unclealex.hammers.calendar.server.html;

import java.io.IOException;
import java.net.URI;
import java.util.SortedSet;

import org.cdmckay.coffeedom.Document;

import com.google.common.collect.Sets;

/**
 * A base class for {@link HtmlGamesScanner}s that require state. Really, this is to avoid writing Spring factories. 
 * @author alex
 *
 */
public abstract class StatefulDomBasedHtmlGamesScanner extends DomBasedHtmlGamesScanner {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SortedSet<GameUpdateCommand> scan(URI uri, Document document) throws IOException {
		Scanner scanner = createScanner(uri, document);
		scanner.scan();
		return scanner.getGameUpdateCommands();
	}
	
	/**
	 * Create a scanner to scan the games.
	 * @param document The XML document to scan.
	 * @return A scanner as described above.
	 */
	protected abstract Scanner createScanner(URI uri, Document document);

	/**
	 * An abstract to allow for scanning state to be stored, mainly so that there is
	 * no need to muck around with spring-based factories.
	 * @author alex
	 *
	 */
	abstract class Scanner {
		
		private final SortedSet<GameUpdateCommand> i_gameUpdateCommands = Sets.newTreeSet();
		private final URI i_uri;
		private final Document i_document;
		
		public Scanner(URI uri, Document document) {
			super();
			i_uri = uri;
			i_document = document;
		}

		public abstract void scan() throws IOException;
		
		/**
		 * @return the gameUpdateCommands
		 */
		public final SortedSet<GameUpdateCommand> getGameUpdateCommands() {
			return i_gameUpdateCommands;
		}

		/**
		 * 
		 * @return The URI of the page being scanned.
		 */
		public final URI getUri() {
			return i_uri;
		}

		/**
		 * 
		 * @return The document being scanned.
		 */
		public final Document getDocument() {
			return i_document;
		};
	}	
}
