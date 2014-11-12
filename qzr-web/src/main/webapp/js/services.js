'use strict';

/* Services */

var qzrServices = angular.module('qzr.services', ['ngResource']);

qzrServices.factory('qzrSvc', ['$http', function($http) { 
    
    var svc = {
    		
        model : {
            questions : [],
            knowns : [],
            hrmax: null
        },

        loadQuestions: function(callback) {
            $http.get('/api/quizzes/health/questions')
	            .success(function(questions) {
	            	svc.model.questions = questions;
	            });
	        $http.get('/api/quizzes/health/results/hrmax')
	        	.success(function(hrmax) {
	        		svc.model.hrmax = hrmax;
	        	})
	        	.error(function() {
	        		svc.model.hrmax = null;
	        	});
        },

		answer : function(question, answerValue) {
        	console.log("Answering : " + question.key + "=" + answerValue);
        	
        	var answer = { key: question.key, value: answerValue };
            
            $http.put('/api/quizzes/health/questions/' + question.key + '/answer', answer)
                 .success(svc.loadQuestions);
        },

        skip : function(question) {
        	console.log("Skipping : " + question.key);
        	
            $http.post('/api/quizzes/health/questions/' + question.key + '/skip')
                 .success(svc.loadQuestions);
        },

        retractAnswer : function(question) {
        	console.log("Retracting : " + question.key);
        	
            $http.delete('/api/quizzes/health/questions/' + question.key + '/answer')
                 .success(svc.loadQuestions);
        } 

    };
    
    return svc;
    
}]);

