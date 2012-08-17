package uk.co.unclealex.hammers.calendar.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;

public class LeaguePlace extends SeasonAwarePlace {

	public LeaguePlace(int season) {
		super(season);
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof LeaguePlace) && isEqual((SeasonAwarePlace) other);
	}
	
	@Override
	public SeasonAwarePlace withSeason(int season) {
		return new LeaguePlace(season);
	}
	
	@Override
	public void accept(HammersPlaceVisitor visitor) {
		visitor.visit(this);
	}
	
	public static class Tokenizer extends SeasonAwarePlace.Tokenizer<LeaguePlace> implements PlaceTokenizer<LeaguePlace> {

		@Override
		protected LeaguePlace getPlace(int season) {
			return new LeaguePlace(season);
		}
	}
}
