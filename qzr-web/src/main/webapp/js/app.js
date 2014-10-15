'use strict';


// Declare app level module which depends on filters, and services
angular.module('qzr', [
  'ngRoute',
  'qzr.filters',
  'qzr.services',
  'qzr.directives',
  'qzr.controllers'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.
  when('/quizzes/health', {
	  templateUrl: 'partials/health-quiz.html', 
	  controller: 'HealthQuizCtrl'
  }).
  otherwise({
	  redirectTo: '/quizzes/health'
  });
}]);
