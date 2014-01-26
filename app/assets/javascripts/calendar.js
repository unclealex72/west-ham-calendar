var app = angular.module('calendar', ['ui.bootstrap']);

app.config(['$routeProvider', function($routeProvider) {
  $routeProvider.
    when('/season', {templateUrl: 'assets/partials/season.html',   controller: MainCtrl}).
    when('/season/:season/tickets/:ticketType', {templateUrl: 'assets/partials/season.html', controller: MainCtrl}).
    otherwise({redirectTo: '/season'});
}]);

app.filter('customDate', ['$filter', function($filter, $log) {
  return function(input, includeMonth) {
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
	  var includeMinutes = date.getMinutes() != 0;
	  var formatString = "EEE d'" + daySuffix + "'" + (includeMonth?" MMM":"") + ", h" + (includeMinutes?":mm":"") + "a";
	  return $filter('date')(input, formatString).replace("PM", "pm").replace("AM", "am");
  }
}]);

var PRIORITY_POINT = {name: "PriorityPoint", label: "Priority point"};
var ticketTypes = new Array(
  {name: "Bondholder", label: "Bondholder"},
  PRIORITY_POINT,
  {name: "SeasonTicket", label: "Season"},
  {name: "Academy", label: "Academy members"},
  {name: "GeneralSale", label: "General sale"});

function MainCtrl($scope, $http, $location, $routeParams, $filter) {
  $http.post('base.json').success(function(base, status) {
    $scope.currentSeason = ($routeParams.season) ? parseInt($routeParams.season) : parseInt(base.year);
    $scope.ticketType = _.find(ticketTypes, function(ticketType) { return $routeParams.ticketType == ticketType.name });
    if (!$scope.ticketType) {
      $scope.ticketType = PRIORITY_POINT;
    }
    $scope.ticketTypes = ticketTypes
    $scope.user = base.name;
    $scope.authorised = $scope.user ? 1 : 0;
    populateDropdowns($scope, $http, $location);
    populateGames($scope, $http, $filter)
  });
}

function populateDropdowns($scope, $http, $location) {
  var gotoCal = function(season, ticketType) {
    $location.path("/season/" + season + "/tickets/" + ticketType.name)
  };
  $scope.setCurrentSeason = function(season) {
    gotoCal(season, $scope.ticketType);
  };
  $scope.setTicketType = function(ticketType) {
    gotoCal($scope.currentSeason, ticketType);
  };  
  $http.post('seasons.json').success(function(seasons, status) {
    $scope.seasons = _(seasons).map('year').sort().reverse().value();
  });
};

function populateGames($scope, $http, $filter) {
  $http.post($scope.currentSeason + '/games.json').success(function(games, status) {
    var gamesByMonth = _.groupBy(games, function(game) {
      return $filter('date')(game.at, 'MMMM yyyy');
    });
    var monthOrGames = new Array();
    for(var month in gamesByMonth){
      monthOrGames.push({"month": month, "type": "month"});
      _.forEach(gamesByMonth[month], function(game) {
        monthOrGames.push(_.assign(game, {"type": "game"}));
      });
    }
    $scope.games = monthOrGames;
  });
  var attendOrUnattend = function(path, resetValue, game) {
    $http.put('/' + path + '/' + game.id).error(function(data, status, headers, config) {
      game.attended = resetValue;
      alert("This game could not be updated: " + status)
    });
  };
  $scope.attendGame = _.partial(attendOrUnattend, 'attend', false);
  $scope.unattendGame = _.partial(attendOrUnattend, 'unattend', true);
};

