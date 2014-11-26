'use strict';


// Declare app level module which depends on filters, and services
angular.module('qzr', [
  'ngRoute',
  'ui.bootstrap',
  'qzr.filters',
  'qzr.services',
  'qzr.directives',
  'qzr.controllers'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.
  when('/quizzes/hrmax', {
	  templateUrl: 'partials/hrmax-quiz.html'
  }).
  otherwise({
	  redirectTo: '/quizzes/hrmax'
  });
}]);
