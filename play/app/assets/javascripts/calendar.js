var app = angular.module('calendar', ['navigation', 'season', 'game', 'ui.bootstrap']);

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