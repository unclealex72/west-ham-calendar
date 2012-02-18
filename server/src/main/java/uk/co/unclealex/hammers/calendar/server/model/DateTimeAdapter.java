package uk.co.unclealex.hammers.calendar.server.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;

/**
 * Small class to adapt Joda time to JAXB.
 * @author alex
 *
 */
public class DateTimeAdapter 
    extends XmlAdapter<String, DateTime>{
 
    public DateTime unmarshal(String v) throws Exception {
        return new DateTime(v);
    }
 
    public String marshal(DateTime v) throws Exception {
        return v.toString();
    }
 
}