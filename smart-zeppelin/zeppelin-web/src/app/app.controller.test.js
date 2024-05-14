/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
describe('Controller: MainCtrl', function() {
  beforeEach(angular.mock.module('zeppelinWebApp'));

  var scope;
  var rootScope;

  beforeEach(inject(function($controller, $rootScope) {
    rootScope = $rootScope;
    scope = $rootScope.$new();
    $controller('MainCtrl', {
      $scope: scope
    });
  }));

  it('should attach "asIframe" to the scope and the default value should be false', function() {
    expect(scope.asIframe).toBeDefined();
    expect(scope.asIframe).toEqual(false);
  });

  it('should set the default value of "looknfeel to "default"', function() {
    expect(scope.looknfeel).toEqual('default');
  });

  it('should set "asIframe" flag to true when a controller broadcasts setIframe event', function() {
    rootScope.$broadcast('setIframe', true);
    expect(scope.asIframe).toEqual(true);
  });

});
