var app = angular.module('calendar', ['navigation', 'season', 'game', 'ui.bootstrap']);

app.config(['$routeProvider', 'Constants', function($routeProvider, Constants) {
  var defaultSeason = _.max(Constants.seasons);
  var defaultTicketType = _.find(Constants.ticketTypes, function(ticketType) { return ticketType['default']; });
  $routeProvider.
    when('/season/:season/tickets/:ticketType', {templateUrl: 'assets/partials/season.html', controller: 'SeasonCtrl'}).
    when('/season/:season/tickets/:ticketType/game/:gameId', {templateUrl: 'assets/partials/game.html', controller: 'GameCtrl'}).
    otherwise({redirectTo: '/season/' + defaultSeason + '/tickets/' + defaultTicketType.name});
}]);

app.filter('customDate', ['$filter', function($filter, $log) {
  return function(input, format) {
	  if (!input) {
		  return;
	  }
	  var date = new Date(input);
	  var day = date.getDate();
	  var daySuffixDiscriminator = day % 10;
	  var daySuffix;
	  if (daySuffixDiscriminator == 1 && day != 11) {
		  daySuffix = "st";
	  }
	  else if (daySuffixDiscriminator == 2 && day != 12) {
		  daySuffix = "nd";
	  }
	  else if (daySuffixDiscriminator == 3 && day != 13) {
		  daySuffix = "rd";
	  }
	  else {
		  daySuffix = "th";
	  }
	  format = format.replace("dth", "d'" + daySuffix + "'");
	  format = format.replace(":mm!", date.getMinutes() == 0 ? "" : ":mm");
	  return $filter('date')(input, format).replace("PM", "pm").replace("AM", "am");
  }
}]);