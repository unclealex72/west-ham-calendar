/**
 * This package contains all the models used in the West Ham Calendar application.
 */
@TypeDefs({ @TypeDef(name = "dateTime", defaultForType = DateTime.class, typeClass = JodaDateTimeUserType.class) })
@XmlJavaTypeAdapters({ @XmlJavaTypeAdapter(type = DateTime.class, value = DateTimeAdapter.class) })
package uk.co.unclealex.hammers.calendar.model;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.joda.time.DateTime;

