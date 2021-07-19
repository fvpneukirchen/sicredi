# SICREDI challenge
This project is part of hiring process for SICREDI Application Developer job.

## How to Run
### Dependencies
- Java 11
- Maven

### Running application
Once you have Maven and Java 8 installed locally you just need to run ``mvn clean spring-boot:run`` to test it.
After this, you can proceed with tests and verify services documentation in the follow URL:
- SWAGGER: [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)

## Testing
### Unit Tests
For unit testing I'm isolating the layers I have to really test only that unit.
- For controllers, I'm using spring-test framework ``WebMvcTest`` and the tests I proceed are pretty much if the exceptions I expect are returning the messages I set and if the result I'm returning is the JSON I want;
- For services, I'm using ``Mockito`` framework only in order to mock repository layer

### Postman Test
- Adding a Postman collection with pre-configure tests. Feel free to use.

## Code
I believe in clean code and SOLID concepts as the main guideline to code. 
Because of that, you will not see comments everywhere once the code should be sufficiently self-explanatory.
Also, the package organization is layer separated once spring-boot many times works better this way, like if necessary define packages to scan.

### SpringBoot
Following the idea of easily start, auto-configurable and full of useful resources applications I choose to use spring-boot because it gives me an end-to-end option to create services.

### Database
I opted to an in-memory embedded H2 database because it is easy to work with. You can access the console through: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) and the connection string will be available on the startup logs.

**e.g.** _H2 console available at '/h2-console'. Database available at ``'jdbc:h2:mem:fd4c16fd-b58a-45ff-bc43-3d2daa5ff787'``_

### Messaging
I opted to an in-memory embedded ``ActiveMQ`` because it is easy to work with.

### Documentation
For service documentation I used ``Swagger``, adding descriptions to the services exposed.

### API Versioning
I'm following ``URI Versioning`` and this can be easily achieved using proper mappers. For this project, ``mapstruct`` [https://mapstruct.org/](https://mapstruct.org/) is being used to generate mapper between DTO and Entities.

### Lombok
This project uses ``Lombok`` [https://projectlombok.org/](https://projectlombok.org/) which automatically generates  boilerplate code.

### Logging
This project uses the Annotation ``@Log4j2`` from ``Lombok``.


