# Cordova / Phonegap plugin for making native POST and GET calls

## Usage
### Ionic Platform
```javascript
$ionicPlatform.ready(function() {
if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
let data = "somedata"; // you can use an Object too as {id: '1', message: 'hello'}
window.CordovaHttpPlugin.post("https://www.google.com", data, {"cookie": "name=hello", "content-type": "application/xml"},
function(response) {
console.log(response)
},function(response) {
console.error(response)
});
}
}
```

### Not AngularJS or Ionic

This plugin registers a `CordovaHttpPlugin` global on window

### POST AND GET EXAMPLE
Execute a POST request.  Takes a URL, parameters, and headers.
```javascript
let data = "somedata"; // you can use an Object too as {id: '1', message: 'hello'}
window.CordovaHttpPlugin.post("https://www.google.com", data, {"cookie": "name=hello", "content-type": "application/xml"},
function(response) {
console.log(response)
},function(response) {
console.error(response)
});
window.CordovaHttpPlugin.get("https://www.google.com", data, {"cookie": "name=hello", "content-type": "application/xml"},
function(response) {
console.log(response)
},function(response) {
console.error(response)
});
```
- SUCCESS
The success function receives a response object with 3 properties: status, data, and headers.  Status is the HTTP response code. Data is the response from the server as a string. Headers is an object with the headers.
```javascript
{
status: 200,
data: "{'message': 'hello world'}",
headers: {
"Content-Length": "200"
}
}
```
- FAILURE
The error function receives a response object with 2 properties: status, error.  Status is the HTTP response code.  Error is the error response from the server as a string.
```javascript
{
status: 404,
data: "Not Found",
}
```
## Cookies
- a cookie set by a request isn't sent in subsequent requests
Take this into account when using this plugin in your application.

## Helpful
This project it based on an existing project listed below.
* [cordova-HTTP](https://github.com/serviewcare/cordova-HTTP) - Cordova / Phonegap plugin for communicating with HTTP servers. Allows for SSL pinning!

## License

MIT License
