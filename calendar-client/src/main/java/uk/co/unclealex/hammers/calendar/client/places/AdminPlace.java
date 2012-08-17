package uk.co.unclealex.hammers.calendar.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;

public class AdminPlace extends SeasonAwarePlace {

	public AdminPlace(int season) {
		super(season);
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof AdminPlace) && isEqual((SeasonAwarePlace) other);
	}
	
	@Override
	public SeasonAwarePlace withSeason(int season) {
		return new AdminPlace(season);
	}
	
	@Override
	public void accept(HammersPlaceVisitor visitor) {
		visitor.visit(this);
	}
	
	public static class Tokenizer extends SeasonAwarePlace.Tokenizer<AdminPlace> implements PlaceTokenizer<AdminPlace> {

		@Override
		protected AdminPlace getPlace(int season) {
			return new AdminPlace(season);
		}

	}
}
