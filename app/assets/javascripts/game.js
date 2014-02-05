var app = angular.module('game', ['attendance', 'navigation']);

app.controller('GameCtrl', ['$scope', '$http', 'Navigation', 'Attendance',
  function($scope, $http, Navigation, Attendance) {
    $scope.ticketType = Navigation.currentTicketType();
    $http.get('game/' + Navigation.currentGameId()).success(function(game, status) {
      $scope.game = game;
      $scope.attend = Attendance.attendGame;
      $scope.unattend = Attendance.unattendGame;
      $scope.ticketType = Navigation.currentTicketType();
    });
  }
]);
