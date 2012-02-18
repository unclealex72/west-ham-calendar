@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(type=DateTime.class, 
        value=DateTimeAdapter.class)
})
package uk.co.unclealex.hammers.calendar.server.model;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.joda.time.DateTime;

