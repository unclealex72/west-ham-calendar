import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarFeed;

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

/**
 * @author aj016368
 *
 */
public class CalendarLister {

  private final static Pattern PATTERN = Pattern.compile("/([^/]*\\.calendar\\.google\\.com)");
  
  public static void main(String[] args) throws Exception {
    Map<String, String> announcedCalendars = mapAnnouncedCalendars();
    CalendarService calendarService = new CalendarService("uk.co.unclealex.hammers");
    calendarService.setUserCredentials("unclealex72@gmail.com", "play4Itx");
    CalendarFeed feed = 
        calendarService.getFeed(new URL("https://www.google.com/calendar/feeds/default/owncalendars/full"), CalendarFeed.class);
    for (CalendarEntry entry : feed.getEntries()) {
      TextConstruct calendarTitle = entry.getTitle();
      String title = calendarTitle.getPlainText();
      String calendarId = entry.getId();
      System.out.format("Found calendar %s at %s\n", title, calendarId);
      if (title.startsWith("West Ham") || title.contains("@")) {
        Matcher m = PATTERN.matcher(calendarId);
        if (m.find()) {
          String id = m.group(1);
          String announcedCalendarName = announcedCalendars.get(id);
          if (announcedCalendarName == null) {
            if (title.toLowerCase().contains("attended")) {
              System.out.format("  Keeping calendar %s [%s] as it is a private calendar\n", id, title);
            }
            else {
              System.out.format("  Removing calendar %s [%s]\n", id, title);
              entry.delete();
            }
          }
          else {
            System.out.format("  Keeping calendar %s [%s] as it matches \"%s\"\n", id, title, announcedCalendarName);
            announcedCalendars.remove(id);
          }
        }
      }
    }
    for (Entry<String, String> entry : announcedCalendars.entrySet()) {
      System.out.format("Calendar %s \"%s\" was not matched.\n", entry.getKey(), entry.getValue());
    }
  }

  protected static Map<String, String> mapAnnouncedCalendars() throws JDOMException, IOException {
    Map<String, String> calendars = new TreeMap<String, String>();
    SAXBuilder builder = new SAXBuilder();
    Document document = builder.build(CalendarLister.class.getClassLoader().getResourceAsStream("existingcalendars.xml"));
    Predicate<Object> filter = new Predicate<Object>() {
      @Override
      public boolean apply(Object el) {
        return el instanceof Element && "a".equals(((Element) el).getName());
      }
    };
    for (@SuppressWarnings("unchecked")
    Iterator<Element> iter = Iterators.filter(document.getRootElement().getDescendants(), filter); iter.hasNext(); ) {
      Element el = iter.next();
      String href = el.getAttributeValue("href");
      String title = el.getTextNormalize();
      Matcher m = PATTERN.matcher(href);
      if (m.find()) {
        String id = m.group(1);
        calendars.put(id, title);
      }
    }
    return calendars;
  }
}
