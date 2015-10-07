This is a REST service for retrieving exchange rate for the ruble. As a data source for rates used API of [the Central Bank of the Russia Federation](http://www.cbr.ru/scripts/Root.asp).  

The service based on Spring Boot and use LRU cache with Ehcache. The cache store last "N" exchange rates (currently, 100 elements). 

To run tests separately:
------------------------

Use ./gradlew test

To build a single executable JAR file:
--------------------------------------

Use ./gradlew clean build

To run service:
---------------

Use java -jar build/libs/currency-rate-api-0.0.1.jar

To test service:
---------------

Visit [http://localhost:8080/rate/USD/2015-10-06] (http://localhost:8080/rate/USD/2015-10-06), where you see exchange rate for USD at 2015-10-06:

```json
{"code":"USD","rate":"65.6248","date":"2015-10-06"}
```

Visit [http://localhost:8080/rate/USD] (http://localhost:8080/rate/USD), where you see exchange rate for USD at tomorrow.


### Dependencies:

[Java 1.8]

[Spring Boot]

[Ehcache]

[Java 1.8]: http://www.oracle.com/technetwork/java/javase/documentation/api-jsp-136079.html

[Spring Boot]: http://projects.spring.io/spring-boot/

[Ehcache]: http://www.ehcache.org/