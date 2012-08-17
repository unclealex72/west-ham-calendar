package uk.co.unclealex.hammers.calendar.client.places;

public interface HammersPlaceVisitor {

	void visit(TeamsPlace teamsPlace);
	void visit(LeaguePlace leaguePlace);
	void visit(GamesPlace gamesPlace);
	void visit(AdminPlace adminPlace);
	void visit(NoGamesPlace noGamesPlace);
	void visit(HammersPlace hammersPlace);

}
