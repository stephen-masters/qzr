'use strict';

/* Services */

var qzrServices = angular.module('qzr.services', ['ngResource']);

qzrServices.factory('qzrSvc', ['$http', function($http) { 

	var svc = {
    	
		initialised: false,
    	eventListeners: [],
		socket : null,
		stompClient : null,
		connected : false,

        model : {
            questions : [],
            knowns : [],
            hrmax: null
        },

        connect: function() {
			// Assumes that service is on same server and that we have been consistent with ports.
			svc.socket = new SockJS('/drools');
			svc.stompClient = Stomp.over(svc.socket);
			svc.stompClient.connect({}, function(frame) {
				svc.connected = true;
				console.log('Connected to /drools : ' + frame);
				svc.stompClient.subscribe('/topic/agendaevents/', svc.newEvent );
			});
        },
        
        newEvent: function(data) {
			var event = JSON.parse(data.body);
			svc.notifyEventListeners(event);
        },

        disconnect: function() {
            svc.stompClient.disconnect();
            svc.setConnected(false);
            console.log("Disconnected Drools working memory event listener client");
        },
        
        onEvent : function(callback) {
        	if (!_.contains(svc.eventListeners, callback)) svc.eventListeners.push(callback);
        },
        
        notifyEventListeners : function(event) {
        	_.each(svc.eventListeners, function(callback) {
        		callback.call(undefined, event);
        	});
        },

        loadQuestions: function() {
            $http.get('/api/quizzes/health/questions')
	            .success(function(questions) { 
	            	svc.model.questions = questions;
	    	        $http.get('/api/quizzes/health/results/hrmax')
		        		.success(function(hrmax) {
		        			svc.model.hrmax = hrmax;
		        	        $http.get('/api/quizzes/health/knowns')
			    				.success(function(knowns) {
			    					svc.model.knowns = knowns;
			    					svc.connect();
			    				});
		        		})
		        		.error(function() { svc.model.hrmax = null; });
	            });
        },
        
       
        init: function() {
        	if (!svc.initialised) svc.loadQuestions();
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

