package uk.co.unclealex.hammers.calendar.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;

public class MainPlace extends HammersPlace {

	@Override
	public void accept(HammersPlaceVisitor visitor) {
		visitor.visit(this);
	}
	
	public static class Tokenizer implements PlaceTokenizer<MainPlace> {

		@Override
		public MainPlace getPlace(String token) {
			return new MainPlace();
		}

		@Override
		public String getToken(MainPlace place) {
			return "";
		}

	}
}
