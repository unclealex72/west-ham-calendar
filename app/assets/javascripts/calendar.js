angular.module('calendar', ['ui.bootstrap']).
  config(['$routeProvider', function($routeProvider) {
    $routeProvider.
      when('/season', {templateUrl: 'assets/partials/season.html',   controller: SeasonCtrl}).
      when('/season/:season', {templateUrl: 'assets/partials/season.html', controller: SeasonCtrl}).
      otherwise({redirectTo: '/season'});
}]);

function SeasonsCtrl($scope, $http, $location) {
  $scope.setCurrentSeason = function(season) {
    $scope.currentSeason = season;
    $location.path("/season/" + season);
  };
  $http.post('seasons.json').success(function(seasons, status) {
    $scope.seasons = _.map(seasons, 'year');
    $scope.currentSeason = _.max($scope.seasons);
  });
};

function SeasonCtrl($scope, $http, $routeParams) {
  var gamesSupplier = function(season) {
    $http.post(season + '/games.json').success(function(games, status) {
      $scope.season = parseInt(season);
      $scope.games = games;
    });
  };
  if ($routeParams.season) {
    gamesSupplier($routeParams.season);
  }
  else {
    $http.post('/latestSeason.json').success(function(latestSeason, status) {
      gamesSupplier(latestSeason.year);
    });
  }
};
