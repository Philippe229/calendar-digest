'use strict';
var app = angular.module('app', ['ngRoute']);
app.config(function($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);
    $routeProvider
        .when('/', {templateUrl: 'pages/login.html'})
        .when('/settings', {templateUrl: 'pages/settings.html'})
});