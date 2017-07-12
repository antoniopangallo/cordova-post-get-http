/* global angular */
/*
 * An HTTP Plugin for PhoneGap.
 */

var exec = require('cordova/exec');

function mergeHeaders (globalHeaders, localHeaders) {
  var globalKeys = globalHeaders ? Object.keys(globalHeaders) : [];
  var key;
  for (var i = 0; i < globalKeys.length; i++) {
    key = globalKeys[i];
    if (!localHeaders.hasOwnProperty(key)) {
      localHeaders[key] = globalHeaders[key];
    }
  }
  return localHeaders;
}

var http = {
  headers: {},
  cacheResults: false,
  sslPinning: false,
  post: function (url, params, headers, success, failure) {
    headers = mergeHeaders(this.headers, headers);
    return exec(success, failure, 'CordovaHttpPlugin', 'post', [url, params, headers]);
  },
  get: function (url, params, headers, success, failure) {
    headers = mergeHeaders(this.headers, headers);
    return exec(success, failure, 'CordovaHttpPlugin', 'get', [url, params, headers, this.cacheResults]);
  }
};

module.exports = http;

if (typeof angular !== 'undefined') {
  angular.module('cordovaHTTP', []).factory('cordovaHTTP', function ($timeout, $q) {
    function makePromise (fn, args, async) {
      var deferred = $q.defer();

      var success = function (response) {
        if (async) {
          $timeout(function () {
            deferred.resolve(response);
          });
        } else {
          deferred.resolve(response);
        }
      };

      var fail = function (response) {
        if (async) {
          $timeout(function () {
            deferred.reject(response);
          });
        } else {
          deferred.reject(response);
        }
      };

      args.push(success);
      args.push(fail);

      fn.apply(http, args);

      return deferred.promise;
    }

    var cordovaHTTP = {
      post: function (url, params, headers) {
        return makePromise(http.post, [url, params, headers], true);
      },
      get: function (url, params, headers) {
        return makePromise(http.get, [url, params, headers], true);
      }
    };
    return cordovaHTTP;
  });
} else {
  window.CordovaHttpPlugin = http;
}
