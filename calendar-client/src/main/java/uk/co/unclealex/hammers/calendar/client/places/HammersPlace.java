package uk.co.unclealex.hammers.calendar.client.places;

import com.google.gwt.place.shared.Place;

public abstract class HammersPlace extends Place {

	public abstract void accept(HammersPlaceVisitor visitor);
}
