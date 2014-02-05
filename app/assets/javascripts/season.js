var app = angular.module('season', ['navigation', 'attendance']);

var createDirective = function(directiveName, extraScope) {
  app.directive(directiveName, [function() {
    var scope = _.assign({game: '=', 'class': '@'}, extraScope);
    return {
      restrict: 'E',
      replace: true,
      scope: scope,
      templateUrl: 'assets/partials/' + directiveName + '.html'
    };
  }]);
};
createDirective('gameOpponents');
createDirective('gameWhen');
createDirective('gameWhere', {showLink: '@'});
createDirective('gameResult', {missingText: '='});

app.controller('SeasonCtrl', ['$scope', '$http', '$filter', 'Navigation', 'Attendance',
function($scope, $http, $filter, Navigation, Attendance) {
  $scope.ticketType = Navigation.currentTicketType();
  $http.get(Navigation.currentSeason() + '/games').success(function(gamesWrapper, status) {
    var games = gamesWrapper.games;
    var gamesByMonth = _.groupBy(games, function(game) {
      return $filter('date')(game.at, 'MMMM yyyy');
    });
    var gamesByMonthArray = new Array();
    for(var month in gamesByMonth){
      gamesByMonthArray.push({"month": month, "games": gamesByMonth[month]});
    }
    $scope.gamesByMonth = gamesByMonthArray;
    $scope.showGame = function(game) {
      Navigation.goToGame(game);
    };
    $scope.attend = Attendance.attendGame;
    $scope.unattend = Attendance.unattendGame;
  });
}]);
