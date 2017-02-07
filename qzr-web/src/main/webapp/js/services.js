'use strict';

/* Services */

var qzrServices = angular.module('qzr.services', ['ngResource']);

qzrServices.factory('qzrSvc', ['$http', function($http) { 

    var svc = {
        
        initialised: false,
        eventListeners: [],
        socket : null,
        stompClient : null,
        isConnecting : false,
        isConnected : false,

        model : {
            questions : [],
            knowns : [],
            hrmax: null,
            events: []
        },

        connect: function() {
            // If for some reason this gets called twice, we try to avoid connecting again.
            if (svc.isConnecting || svc.isConnected) return;

            svc.isConnecting = true;

            // Assumes that service is on same server and that we have been consistent with ports.
            svc.socket = new SockJS('/drools');
            svc.stompClient = Stomp.over(svc.socket);
            svc.stompClient.connect({}, function(frame) {
                svc.isConnected = true;
                svc.isConnecting = false;
                console.log('Connected to /drools : ' + frame);
                svc.stompClient.subscribe('/queue/agendaevents/', svc.newEvent );
            });
        },

        newEvent: function(data) {
            var event = JSON.parse(data.body);
            svc.model.events.push(event);
        },

        disconnect: function() {
            svc.stompClient.disconnect();
            svc.setConnected(false);
            console.log("Disconnected Drools working memory event listener client");
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
                            });
                        })
                        .error(function() { svc.model.hrmax = null; });
                });
        },

        init: function() {
            if (!svc.initialised) svc.loadQuestions();
            svc.connect();
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

