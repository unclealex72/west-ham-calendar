package uk.co.unclealex.hammers.calendar.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;

public class NoGamesPlace extends HammersPlace {

	public NoGamesPlace() {
		super();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof NoGamesPlace;
	}
	
	@Override
	public void accept(HammersPlaceVisitor visitor) {
		visitor.visit(this);
	}
	
	public static class Tokenizer implements PlaceTokenizer<NoGamesPlace> {

		@Override
		public NoGamesPlace getPlace(String token) {
			return new NoGamesPlace();
		}

		@Override
		public String getToken(NoGamesPlace place) {
			return null;
		}

	}
}
