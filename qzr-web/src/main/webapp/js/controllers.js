'use strict';

/* Controllers */

var qzrControllers = angular.module('qzr.controllers', []);

qzrControllers.controller('HrMaxQuizCtrl', [
    '$scope', 'qzrSvc',
    function($scope, qzrSvc) {

        $scope.$watch(
            function() { return qzrSvc.model.events; },
            function(newVal, oldVal) { 
                $scope.events = qzrSvc.model.events; 
            },
            true
        );

        $scope.$watch(
            function() { return qzrSvc.model.questions; },
            function(newVal, oldVal) { 
                $scope.questions = qzrSvc.model.questions; 
            },
            true
        );

        $scope.$watch(
            function() { return qzrSvc.model.answers; },
            function(newVal, oldVal) { 
                $scope.answers = qzrSvc.model.answers; 
            },
            true
        );

        $scope.$watch(
            function() { return qzrSvc.model.knowns; },
            function(newVal, oldVal) { 
                $scope.knowns = qzrSvc.model.knowns; 
            },
            true
        );

        $scope.$watch(
            function() { return qzrSvc.model.hrmax; },
            function(newVal, oldVal) { 
                $scope.hrmax = qzrSvc.model.hrmax; 
            },
            true
        );

        $scope.$watch(
            function() { return qzrSvc.model.events; },
            function(newVal, oldVal) { 
                $scope.events = qzrSvc.model.events; 
            },
            true
        );

        $scope.isAnswered = function(question) {
            return question.answer != null;
        };
        $scope.isNotAnswered = function(question) {
            return !$scope.isAnswered(question);
        };

        $scope.answer = function(question, answer) {
            qzrSvc.answer(question, answer);
        };

        $scope.skip = function(question) {
            qzrSvc.skip(question);
        };

        $scope.retract = function(question) {
            qzrSvc.retractAnswer(question);
        };

        function init() {
            qzrSvc.init();
        }

        init();

    }
]);

