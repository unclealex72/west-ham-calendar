package uk.co.unclealex.hammers.calendar.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;

public class GamesPlace extends SeasonAwarePlace {

	public GamesPlace(int season) {
		super(season);
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof GamesPlace) && isEqual((SeasonAwarePlace) other);
	}
	
	@Override
	public SeasonAwarePlace withSeason(int season) {
		return new GamesPlace(season);
	}
	
	@Override
	public void accept(HammersPlaceVisitor visitor) {
		visitor.visit(this);
	}
	
	public static class Tokenizer extends SeasonAwarePlace.Tokenizer<GamesPlace> implements PlaceTokenizer<GamesPlace> {

		@Override
		protected GamesPlace getPlace(int season) {
			return new GamesPlace(season);
		}

	}
}
