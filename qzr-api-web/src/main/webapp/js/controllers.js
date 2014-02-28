'use strict';

/* Controllers */

var qzrControllers = angular.module('qzr.controllers', []);

qzrControllers.controller('HealthQuizCtrl', [
    '$scope', 'qzrSvc',
    function($scope, qzrSvc) {

    	function updateQuestions() {
    		$scope.questions = qzrSvc.model.questions;
    	}
    	
    	qzrSvc.onLoad(function() {
    		updateQuestions();
    	});

	}
]);

