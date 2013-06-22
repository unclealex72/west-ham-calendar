var app = angular.module('calendar', ['ui.bootstrap']);

app.config(['$routeProvider', function($routeProvider) {
  $routeProvider.
    when('/season', {templateUrl: 'assets/partials/season.html',   controller: MainCtrl}).
    when('/season/:season', {templateUrl: 'assets/partials/season.html', controller: MainCtrl}).
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

app.filter('possessive', [function($filter, $log) {
  return function(input) {
    if (!input) {
      return ""
    }
    else if (/[sx]$/.test(input)) {
      return input + "'";
    }
    else {
      return input + "'s";
    }
  };
}]);

function MainCtrl($scope, $http, $location, $routeParams, $filter) {
  $http.post('base.json').success(function(base, status) {
    $scope.currentSeason = ($routeParams.season) ? parseInt($routeParams.season) : parseInt(base.year);
    $scope.user = base.name;
    $scope.authorised = $scope.user ? 1 : 0;
    populateDropdowns($scope, $http, $location);
    populateGames($scope, $http, $filter)
  });
}

function populateDropdowns($scope, $http, $location) {
  $scope.setCurrentSeason = function(season) {
    $scope.currentSeason = parseInt(season);
    $location.path("/season/" + season);
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
  $scope.attendGame = function(game) {
	  $http.put('/attend/' + game.id);
  }
  $scope.unattendGame = function(game) {
	  $http.put('/unattend/' + game.id);
  }
};

