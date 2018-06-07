'use strict';
var app = angular.module('app', []);
app.controller('home', function homeController($scope, $http) {
    $scope.submit = function() {
        var data = { input: $scope.input };
        $http.post("/submit", data);
    }
});