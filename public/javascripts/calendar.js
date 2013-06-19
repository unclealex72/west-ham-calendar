var app = angular.module('calendar', ['ui.bootstrap']);

app.config(['$routeProvider', function($routeProvider) {
  $routeProvider.
    when('/season', {templateUrl: 'assets/partials/season.html',   controller: SeasonCtrl}).
    when('/season/:season', {templateUrl: 'assets/partials/season.html', controller: SeasonCtrl}).
    otherwise({redirectTo: '/season'});
}]);

app.filter('customDate', ['$filter', '$log', function($filter, $log) {
  return function(input) {
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
	  var formatString = "EEE d'" + daySuffix + "', h" + (includeMinutes?":mm":"") + "a";
	  return $filter('date')(input, formatString).replace("PM", "pm").replace("AM", "am");
  }
}]);

function SeasonsCtrl($scope, $http, $location, $routeParams) {
  $scope.setCurrentSeason = function(season) {
    $scope.currentSeason = season;
    $location.path("/season/" + season);
  };
  $http.post('seasons.json').success(function(seasons, status) {
    $scope.seasons = _(seasons).map('year').sort().reverse().value();
    currentSeason($http, $routeParams, function(currentSeason) {
      $scope.currentSeason = currentSeason;  
    }); 
  });
};

function SeasonCtrl($scope, $http, $routeParams, $filter) {
  var gamesSupplier = function(season) {
    $http.post(season + '/games.json').success(function(games, status) {
      $scope.season = parseInt(season);
      var gamesByMonth = _.groupBy(games, function(game) {
        return $filter('date')(game.at, 'MMMM yyyy');
      });
      var monthOrGames = new Array();
      for(var month in gamesByMonth){
        monthOrGames.push({"month": month});
        _.forEach(gamesByMonth[month], function(game) {
          monthOrGames.push(game);
        });
      }
      $scope.games = monthOrGames;
    });
  };
  currentSeason($http, $routeParams, gamesSupplier);
};

function currentSeason($http, $routeParams, callback) {
  if ($routeParams.season) {
    callback($routeParams.season);
  }
  else {
    $http.post('/latestSeason.json').success(function(latestSeason, status) {
      callback(latestSeason.year);
    });
  }  
}
