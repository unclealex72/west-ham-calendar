package uk.co.unclealex.hammers.calendar.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;

public class TeamsPlace extends SeasonAwarePlace {

	public TeamsPlace(int season) {
		super(season);
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof TeamsPlace) && isEqual((SeasonAwarePlace) other);
	}
	

	@Override
	public SeasonAwarePlace withSeason(int season) {
		return new TeamsPlace(season);
	}
	
	@Override
	public void accept(HammersPlaceVisitor visitor) {
		visitor.visit(this);
	}
	
	public static class Tokenizer extends SeasonAwarePlace.Tokenizer<TeamsPlace> implements PlaceTokenizer<TeamsPlace> {

		@Override
		protected TeamsPlace getPlace(int season) {
			return new TeamsPlace(season);
		}
	}
}
