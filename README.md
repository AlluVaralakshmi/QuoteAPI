# QuoteAPI
Steps to run the application locally:
git clone https://github.com/AlluVaralakshmi/QuoteAPI.git
cd QuoteAPI
mvn clean install
mvn spring-boot:run
In Swagger: http://localhost:8080/swagger-ui/index.html

Backend Endpoints:
GET http://localhost:8080/api/quote

You can configure custom rate limits in the `application.properties` file.  
For example:

properties
# Custom rate limit for /api/test endpoint
ratelimit.test.requests=3       # Allow 3 requests
ratelimit.test.windowMs=30000   # Within 30 seconds
