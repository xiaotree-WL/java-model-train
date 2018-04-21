'use strict';

angular.module('consumer.admin', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/admin', {
        templateUrl: 'angular/insert.html',
        controller: 'admin'
    });
}])
.controller('searchJobViewCtrl', ['$scope', '$http', function($scope, $http) {

    $scope.task_name = "";
    $scope.log_line = "";
    $scope.status = "";

    $scope.searchTextChange = function() {
    }

    $scope.search = function() {
        if ($scope.task_name === "")
            return;
        var url = "/consumer/admin?text="+encodeURIComponent($scope.task_name);
        $http.get(url).then(function (response){
            $scope.status = response["data"]["status"];
        }).catch(function (){
        });
    }
}]);