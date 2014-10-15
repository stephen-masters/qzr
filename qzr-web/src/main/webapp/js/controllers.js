'use strict';

/* Controllers */

var qzrControllers = angular.module('qzr.controllers', []);

qzrControllers.controller('HealthQuizCtrl', [
    '$scope', 'qzrSvc',
    function($scope, qzrSvc) {

    	function updateQuestions() {
    		$scope.questions = qzrSvc.model.questions;
    		
    		_.each($scope.questions, function(q) {
    			$scope.$watch(q.key, function(newval, oldval) {
    				console.log(q.key + ' : ' + newval + '/' + oldval);
    			})
    		});
    	}
    	
    	$scope.answer = function(question) {
    		qzrSvc.answer(question);
    	}
    	
    	qzrSvc.onLoad(function() {
    		updateQuestions();
    	});
    	
	}
]);

