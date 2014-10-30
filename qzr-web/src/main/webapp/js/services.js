'use strict';

/* Services */

var qzrServices = angular.module('qzr.services', ['ngResource']);

qzrServices.factory('qzrSvc', ['$http', function($http) { 
    
    var svc = {

    	isLoaded: false,
    		
        model : {
            questions : [],
            knowns : []
        },
        
        load: function(callback) {
            $http.get('/api/quizzes/health/questions').success(function(quizState) {
            	if (quizState.questions) {
            		svc.model.questions = quizState.questions;
            	}
            	if (quizState.knowns) {
            		svc.model.knowns = quizState.knowns;
            	}
            	svc.notify();
            });
        },
        
        updateQuizState: function(quizState) {
			_.each(quizState.questions, svc.addQuestion);
			_.each(quizState.knowns, svc.addKnown);
			svc.notify();
        },
        
        addQuestion: function(newQuestion) {
        	var found = _.find(svc.model.questions, function(question) { return question.key == newQuestion.key });
        	if (!found) {
 			   svc.model.questions.push(newQuestion);
        	}
        },
        
        addKnown: function(newKnown) {
        	var found = _.find(svc.model.knowns, function(known) { return known.key == newKnown.key });
        	if (!found) {
 			   svc.model.knowns.push(newKnown);
        	}
        },
         
        onLoadCallbacks: [],
        onLoad: function(callback) {
        	if (!_.contains(svc.onLoadCallbacks, callback)) svc.onLoadCallbacks.push(callback);
        },

        notify: function() {
        	_.each(svc.onLoadCallbacks, function(callback) { callback.call(); })
        },
        
        answer : function(question) {
        	console.log("Answering : " + question.key + "=" + question.answer);
        	
            $http.put('/api/quizzes/health/answers', {
            	     key: question.key,
                     answer: question.answer
                 })
                 .success(svc.updateQuizState);
        }

    };
    
    svc.load();
    
    return svc;
    
}]);

