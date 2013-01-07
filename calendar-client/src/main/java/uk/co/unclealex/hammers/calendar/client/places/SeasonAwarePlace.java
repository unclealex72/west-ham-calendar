package uk.co.unclealex.hammers.calendar.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;

public abstract class SeasonAwarePlace extends HammersPlace {

	private final int season;

	public SeasonAwarePlace(int season) {
		super();
		this.season = season;
	}

	public abstract boolean equals(Object other);
	
	public boolean isEqual(SeasonAwarePlace other) {
		return getSeason() == other.getSeason();
	}
	
	public abstract SeasonAwarePlace withSeason(int season);
	
	protected abstract static class Tokenizer<P extends SeasonAwarePlace> implements PlaceTokenizer<P> {
		
		@Override
		public P getPlace(String token) {
			return getPlace(Integer.parseInt(token));
		}
		
		protected abstract P getPlace(int season);

		public String getToken(P place) {
			return Integer.toString(place.getSeason());
		}
		
	}
	public int getSeason() {
		return season;
	}
		
}


