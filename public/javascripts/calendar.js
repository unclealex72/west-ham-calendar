angular.module('calendar', ['ui.bootstrap']).
  config(['$routeProvider', function($routeProvider) {
    $routeProvider.
      when('/season', {templateUrl: 'assets/partials/season.html',   controller: SeasonCtrl}).
      when('/season/:season', {templateUrl: 'assets/partials/season.html', controller: SeasonCtrl}).
      otherwise({redirectTo: '/season'});
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
