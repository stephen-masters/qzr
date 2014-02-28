'use strict';

/* Services */

var qzrServices = angular.module('qzr.services', ['ngResource']);

qzrServices.factory('qzrSvc', ['$http', function($http) { 
    
    var svc = {

    	isLoaded: false,
    		
        model : {
            questions : []
        },
        
        load: function(callback) {
            $http.get('/api/quizzes/health/questions').success(svc.doOnLoad);
        },
         
        onLoadCallbacks: [],
        onLoad: function(callback) {
        	if (!_.contains(svc.onLoadCallbacks, callback)) svc.onLoadCallbacks.push(callback);
        },
        doOnLoad: function(data) {
        	svc.model.questions = data.questions;
        	_.each(svc.onLoadCallbacks, function(callback) { callback.call(); })
        },
        
        answer : function(key, answer, callback) {
            $http.post('/api/quizzes/health/questions/' + key + '/answer')
                 .success(function() {
                     // Do things with the model...
                     
                     // And let the controller know that we're done...
                     callback.call();
                 });
        }

    };
    
    svc.load();
    
    return svc;
    
}]);

