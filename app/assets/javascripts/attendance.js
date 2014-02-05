var app = angular.module('attendance', []);

app.service('Attendance', ['$http', '$window', function($http) {
  var attendOrUnattend = function(path, game) {
    if (!game.busy) {
      game.busy = true;
      setTimeout(function() {
        $http.put('/' + path + '/' + game.id).
        success(function(newGame) {
          game.attended = newGame.attended;
          game.busy = false;
        }).
        error(function(data, status, headers, config) {
          game.busy = false;
          $window.alert("This game could not be updated: " + status)
        });
      }, 5000);
    }
  };
  var service = {
    attendGame: _.partial(attendOrUnattend, 'attend'),
    unattendGame: _.partial(attendOrUnattend, 'unattend')
  };
  return service;
}]);