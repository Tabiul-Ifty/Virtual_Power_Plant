Virtual Power Plant Battery Management System
______________________________________________

#####Overview######

This Spring Boot application manages battery registrations and provides an API for querying battery information within a postcode range.

#####Prerequisites#####

Java 17
Maven
H2 database
I couldn't install the PostgreSQL Database in my system for some reason. That's why I had to use the H2 Database for this project, there are some limitations for this database but the code follows a structure that if the database is changed there will not be any impact.

#####Setup Instructions#####

Clone the repository (Repo link)
Start the application with mvn spring-boot:run

#####Endpoints#######

POST /api/batteries/register: Register batteries
GET /api/batteries/range: Query batteries by postcode range

######Running Tests#######

Run mvn test to execute unit and integration tests
or Right Click on the Root folder and Click Run 'All Tests'

######Additional Features#######

Concurrent battery registration
Comprehensive error handling

######Additional Testing######
API Automation
Performance And Data Driven testing

[NOTE] SOME OF THE TEST CASES MIGHT NOT PROVIDE DESIRED OUTPUT DUE TO THE DATABASE. THE VALUES first NEEDS TO BE SAVED IN DB BEFORE RUNNING THE ASSERTIONS.

Unfortunatly. I could not use Docker for the test container due to my system limitation, however, I have given the docker file structure and other elements as well.
DOCKER RUN TESTS: docker-compose -f docker/docker-compose.test.yml up --build


####Testing Endpoints####
Use Postman or cURL, I have used cURL, it is easy to  use.

#####Commands#####

Post:
________
[1] Valid:
curl -X POST http://localhost:8080/api/batteries/register \
-H "Content-Type: application/json" \
-d '[
    {"name": "Battery ami", "postcode": "1000", "wattCapacity": 1500},
    {"name": "Battery tumi", "postcode": "1500", "wattCapacity": 2800}
]'
_________
[2] Error Case - Empty Battery List:
curl -X POST http://localhost:8080/api/batteries/register \
-H "Content-Type: application/json" \
-d '[]'


Get:
_________
[1] Valid
curl "http://localhost:8080/api/batteries/range?startPostcode=1000&endPostcode=1500"
_________
[2]Error Case - Invalid Postcode Range:
curl "http://localhost:8080/api/batteries/range?startPostcode=abc&endPostcode=def"


::MORE TESTING SCOPES::

- Validate Successful Single Battery Registration.
- Validate Bulk Battery Registration Endpoint.
- Verify Database Persistence of Battery Entries.
- Validate Battery Name Alphabetical Sorting.
- Verify Total Watt Capacity Calculation Accuracy.
- Validate Average Watt Capacity Computation.
- Test Empty Result Set for Non-Matching Postcode Range.
- Check High Volume Battery Registration Stress Test.
- Validate Invalid Data Type Submissions.
- Validate Blocking of Negative Watt Capacity Entries.
- Validate Preventing Duplicate Battery Registrations.
- Check Minimum Watt Capacity Filtering.
- Check Maximum Watt Capacity Filtering.
- Validate API Error Response Mechanisms.
- Check Input Validation Error Logging.
- Check System Event Logging Verification.

::Security measures that could be added::

- JWT authentication
- Input validation
- CORS configuration
- Role-based access

::Possible improvements in the overall system::

- Caching for frequent queries
- API versioning
- More comprehensive logging
- Pagination for large datasets
- Transaction management




