angular.module('compiler-app', [ 'ngRoute' ]).config(function($routeProvider, $httpProvider) {

	$routeProvider.when('/login', {
		templateUrl : 'login.html',
		controller : 'controller',
		controllerAs: 'nav'
	}).otherwise('/');

	$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

}).controller('navigation',

		function($rootScope, $http, $location, $route) {
			
			var self = this;

			self.tab = function(route) {
				return $route.current && route === $route.current.controller;
			};

			var authenticate = function(credentials, callback) {

				var headers = credentials ? {
					authorization : "Basic "
							+ btoa(credentials.username + ":"
									+ credentials.password)
				} : {};

				$http.get('user', {
					headers : headers
				}).then(function(response) {
					if (response.data.name) {
						$rootScope.authenticated = true;
					} else {
						$rootScope.authenticated = false;
					}
					callback && callback($rootScope.authenticated);
				}, function() {
					$rootScope.authenticated = false;
					callback && callback(false);
				});

			}

			authenticate();

			self.credentials = {};
			self.login = function() {
				authenticate(self.credentials, function(authenticated) {
					if (authenticated) {
						console.log("Login succeeded")
						$location.path("/compile");
						self.error = false;
						$rootScope.authenticated = true;
					} else {
						console.log("Login failed")
						$location.path("/");
						self.error = true;
						$rootScope.authenticated = false;
					}
				})
			};
			
			self.compile = function() {
			  console.log('compile the files');
			  var file = $('input[name="uploadfile"').get(0).files[0];
			  var formData = new FormData();
			  formData.append('file', file);
			  $http({
				  url:'/compile',
				  method: 'POST',
				  data: formData,
				  headers:{
					"Content-Type": undefined
				  }
			     }).then(function success(response) {
					$('#errorText').text('');
					$('#error').addClass('hidden');
				    $('#successText').text(response.data.description);
                    $('#success').removeClass('hidden');
				 },function error(response) {
					 $('#successText').text('');
					 $('#success').addClass('hidden');
                     if(response.data){
				      $('#errorText').text(response.data.description);
                      $('#error').removeClass('hidden');
					}else{
					  $('#errorText').text(response.message);
                      $('#error').removeClass('hidden');
					}
				 });
			};

			self.logout = function() {
				$http.post('logout', {}).finally(function() {
					$rootScope.authenticated = false;
					$location.path("/");
				});
			};

		});
