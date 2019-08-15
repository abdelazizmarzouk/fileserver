<p align="center">
    <h1 align="center">HTTP 1.1 Web Based File Server</h1>
</p>

## Introduction

Nowadays most of the applications that serves billions of users are distributed on multiple devices.
There are several communication protocols used to communicate these devices in a client/server fashion or peer to peer.

One of the most successful protocols that is used in all the browsers is <a href="https://www.w3.org/Protocols/rfc2616/rfc2616.html">HTTP 1.1 protocol</a>.
There are multiple frameworks which understands and provide appropriate responses easily  and are built on top of HTTP 1.1 protocol e.g. servlets, rest services, but it could be more interesting to get your hands dirty and provide your own 
implementation to handle one or two types of the methods used in this protocol (e.g. GET, POST, PUT, DELETE).
Building this application will enable you to understand more about sockets, http headers, threading pools and the avoidance of using third party libraries which can be more secure and performant.

There are also unit tests as well as stress tests added to guarantee the reliability of the project. 
A caching mechanism is added for the resources which are read from the file system to improve the performance.
All the servers have multi core processor and therefore multiple threads can be used in one application.
Threading and application distribution on multiple servers play a potential role in  the success of an application to serve millions of users around the globe,
for this reason JMeter testing tool was used to apply stress testing and find the relation between thread pool size and performance and also memory usage at peek times.

## HTTP 1.1 Protocol

All the browsers uses the HTTP protocol to connect to a web server to get resources. 
Once resources are loaded into the browser the browser starts processing the resources displays the website pages, 
displaying pdf files, etc.

The browser sends a request to the server in a specific format and server reacts upon receiving these requests. 
The requests are validated for the correct format and security threats. 

The server starts by reading the first line of the request, e.g. GET /index.html?test=true HTTP/1.1, which contains 
information regarding the HTTP method the URL of the requested resource and HTTP protocol version. The URL can contain 
parameters which should be in UTF-8 as recommended by the World Wide Web Consortium so that there are no incompatibility issues.
The following lines are additional headers that contains information about the browser version, accepted encoding, host, user-agent, cookies, accepted types of files (text/html), etc.
Finally a blank line in the end.

<img src="https://github.com/abdelazizmarzouk/fileserver/blob/master/sequence_diagram.PNG?raw=true">

## Design

### FileServer Class
The file server class contains the main method for starting the application.
It reads the application properties e.g. port, thread pool size, server connection timeout.
It waits on clients to connect and handle the response in a separate thread using thread pool.

### HTTPRequestHandler Class

The HTTPRequestHandler is responsible for handling the incoming requests.
It parses the request and extracts the HTTP Request method (e.g. GET) and assign it to suitable handler e.g. GetRequestHandler.
All the handlers should extend RequestHandler however in this application we only handler GET requests. 
Any exception thrown when handling a request leads to closing the connection with the client after providing a response if possible e.g. bad request, internal server exception.

### GetRequestHandler Class

The GetRequestHandler prepares a response using the HTTPResponseBuilder and writes it to the output stream.
If a resource is not found a page not found response will sent as a response.

### HTTPRequestParser Class

The HTTPRequestParser parses a request and creates the HttpRequest mode.
The request is parsed and validated for the correctness of its format.
Usually most of the browsers are well tested and therefore any request with parsing or encoding problems is not handled.
This is because it is more likely that corrupted http request could impose threats to the server.
Once a request is parsed the model HTTPRequest is generated.

### HTTPResponseBuilder Class
The HTTPResponseBuilder builds a response model.

### HTTPRequest Class

The HTTPRequest models an HTTP request so it contains the method, URL, URL parameters and headers. 
This class does not provide any setters after its creation.

### HTTPResponse Class

The HTTPResponse models the HTTP response so it contains the response status 200, 400, etc, the http reply header, the response content, mime type of the content and headers.

### ResourcesUtil Class

The ResourcesUtil loads a resource from the files system and provides in memory caching for the resource for further calls.
 
### ApplicationPropertiesUtil Class

The properties util class loads the application configuration properties.

  

## Exception Handling

Exceptions are implemented in the application not to break the main thread. 
Two custom exceptions were added one is called **InternalServerException** which is raised
whenever the server is completely not able to handle exceptions. 
The other is **RequestParsingException** which is a custom exception that is raised when there is 
a formatting miss match.
The exceptions are thrown up from the sources so that the socket connections are closed safely and avoid having blocked threads.

## Socket Timeout

Sockets are adjusted to timeout after ten seconds to avoid problems that could happen due to network.

## application.properties

The file application.properties contains information about server configuration. 
If properties are not provided the default values will be used. 
- file.server.port=8000
- file.server.default.computer.name=marshmelo
- file.server.pool.size=80
- file.server.connection.timeout.milliseconds=10000

## Running application

To get the application up and running run the following commands:
- gradle clean
- gradle fatJar
- Go to /build/libs
- Run java -jar fileserver-all-1.0-SNAPSHOT.jar

Please check <a href="https://www.tutorialspoint.com/gradle/index.htm">Gradle</a> tutorial for more information.
 

## Further Improvements
The application covers the required use cases however it is not mature to be used in production and it can be improved by applying the following:

- Checking the length of the request and block very large requests.
- Provide a configurable folder for the static resources e.g. one wants to call it web instead of static.  
- Make it possible to use different <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">status code</a>.
- Add user custom headers and cache controls, for that reason a method called add headers in the HttpResponse was added.

## Stress Testing

JMeter is an open source tool that enables developer to do testing to check the performance.
Below is a table which compare handling **thousand requests** taking into account the thread pool size and file size.

### With Caching
| File Size | Thread Pool Size | Time With Caching| Time Without Caching|
|:--------:|:----:|:-----:|:-----:|
|127B|1|1 Sec|2 Sec|
|127B|10|2 Sec|2 Sec|
|127B|50|2 Sec|2 Sec|
|127B|100|3 Sec|2 Sec|
|127B|500|3 Sec|2 Sec|
|127B|1000|3 Sec|2 Sec|
|710KB|1|4 Sec|8 Sec|
|710KB|10|4 Sec|6 Sec|
|710KB|50|4 Sec|6 Sec|
|710KB|100|4 Sec|6 Sec|
|710KB|500|4 Sec|6 Sec|
|710KB|1000|4 Sec|6 Sec|
|1.32MB|1|12 Sec|20 Sec|
|1.32MB|10|8 Sec|12 Sec|
|1.32MB|50|7 Sec|12 Sec|
|1.32MB|100|7 Sec|12 Sec|
|1.32MB|500|7 Sec|12 Sec|
|1.32MB|1000|7 Sec|12 Sec|

### Conclusion
Thread Pool Size can be parameterized to give the best performance, it is advisable to take into account the resources available on the server.
Adding multiple threads, which can be blocked by other resources that are not able to fulfill all the request, can lead to a very bad performance. 
An application would not affect other applications when each application is running in its own virtual machine however it should be taken into account.
Using cache boosts the performance by factor of seconds when even if only serving three resources.
   


