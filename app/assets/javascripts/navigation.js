var app = angular.module('navigation', ['constants', 'ngRoute', 'ngTouch', 'ngAnimate']);

app.config(['$routeProvider', 'Constants', function($routeProvider, Constants) {
  var defaultSeason = _.max(Constants.seasons);
  var defaultTicketType = _.find(Constants.ticketTypes, function(ticketType) { return ticketType['default']; });
  $routeProvider.
    when('/season/:season/tickets/:ticketType', {templateUrl: 'assets/partials/season.html', controller: 'SeasonCtrl'}).
    when('/season/:season/tickets/:ticketType/game/:gameId', {templateUrl: 'assets/partials/game.html', controller: 'GameCtrl'}).
    otherwise({redirectTo: '/season/' + defaultSeason + '/tickets/' + defaultTicketType.name});
}]);

// Navigation controls
app.service('Navigation', ['$route', '$location', 'Constants', function($route, $location, Constants) {
  var routeParam = function(callback, defaultValue) {
    if ($route.current) {
      return callback();
    }
    else {
      return defaultValue;
    }
  }
  var service = {
    currentSeason: function() {
      return $route.current.params.season;
    },
    currentTicketType: function() {
      return _.find(Constants.ticketTypes, function(ticketType) {
        return ticketType.name == $route.current.params.ticketType;
      });
    },
    currentGameId: function() {
      return $route.current.params.gameId;
    },
    alterSeasonTicketTypeAndGame: function(season, ticketType, game) {
      var path = '/season/' + season + '/tickets/' + ticketType.name
      if (game) {
        path += '/game/' + game.id;
      }
      $location.path(path);
    },
    alterSeason: function(season) {
      service.alterSeasonTicketTypeAndGame(season, service.currentTicketType());
    },
    alterTicketType: function(ticketType) {
      service.alterSeasonTicketTypeAndGame(service.currentSeason(), ticketType);
    },
    goToGame: function(game) {
      service.alterSeasonTicketTypeAndGame(service.currentSeason(), service.currentTicketType(), game);
    },
    onNavigationChange: function($scope, callback) {
      $scope.$on('$routeChangeSuccess', callback);
    }
  };
  return service;
}]);

app.directive('navigation', ['Navigation', 'Constants', function(Navigation, Constants) {
  return {
    restrict: 'E',
    replace: true,
    scope: {
    },
    templateUrl: 'assets/partials/navigation.html',
    link : function($scope, element, attrs) {
      $scope.seasons = _.sortBy(Constants.seasons, function(season) { return -season; });
      $scope.ticketTypes = Constants.ticketTypes;
      Navigation.onNavigationChange($scope, function() {
        $scope.currentSeason = Navigation.currentSeason();
        $scope.currentTicketType = Navigation.currentTicketType();
        $scope.seasonSliders = _.map($scope.seasons, function(season) {
          return { year: season, active: season == $scope.currentSeason };
        });
        $scope.ticketTypeSliders = _.map($scope.ticketTypes, function(ticketType) {
          return { ticketType: ticketType, active: ticketType == $scope.currentTicketType };
        });
      });
      $scope.alterSeason = Navigation.alterSeason;
      $scope.alterTicketType = Navigation.alterTicketType;
      $scope.username = Constants.username;
      $scope.authorised = _.isString(Constants.username);

      $scope.displaySeason = function(season) {
        return season;
      };
      $scope.displayTicketType = function(ticketType) {
        return ticketType.label + " tickets";
      };
    }
  };
}]);

app.directive('slider', [function() {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      slideSelect: '&',
      slideDisplay: '&',
      slides: '='
    },
    templateUrl: 'assets/partials/slider.html',
    link : function($scope, element, attrs) {
      $scope.direction = 'left';
      $scope.currentIndex = 0;

      $scope.setCurrentSlideIndex = function (index) {
        $scope.direction = (index > $scope.currentIndex) ? 'left' : 'right';
        $scope.currentIndex = index;
      };

      $scope.isCurrentSlideIndex = function (index) {
        return $scope.currentIndex === index;
      };

      $scope.previousSlide = function () {
        $scope.direction = 'left';
        $scope.currentIndex = ($scope.currentIndex < $scope.slides.length - 1) ? ++$scope.currentIndex : 0;
      };

      $scope.nextSlide = function () {
        $scope.direction = 'right';
        $scope.currentIndex = ($scope.currentIndex > 0) ? --$scope.currentIndex : $scope.slides.length - 1;
      };
    }
  };
}]);
app.animation('.slide-animation', function () {
    return {
        addClass: function (element, className, done) {
            var scope = element.scope();

            if (className == 'ng-hide') {
                var finishPoint = element.parent().width();
                if(scope.direction !== 'right') {
                    finishPoint = -finishPoint;
                }
                TweenMax.to(element, 0.5, {left: finishPoint, onComplete: done });
            }
            else {
                done();
            }
        },
        removeClass: function (element, className, done) {
            var scope = element.scope();

            if (className == 'ng-hide') {
                element.removeClass('ng-hide');

                var startPoint = element.parent().width();
                if(scope.direction === 'right') {
                    startPoint = -startPoint;
                }

                TweenMax.set(element, { left: startPoint });
                TweenMax.to(element, 0.5, {left: 0, onComplete: done });
            }
            else {
                done();
            }
        }
    };
});
