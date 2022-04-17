# TW Planning REST App

The Planning spring boot application exposes REST endpoints as part of requirements:
- A worker has shifts
- A shift is 8 hours long
- A worker never has two shifts on the same day
- It is a 24 hour timetable 0-8, 8-16, 16-24

# Solution
Build

`mvn clean install`

Run the jar using the following command (runs with java 11)

`<Path_to_java11_java.exe> -jar target/app-0.0.1-SNAPSHOT.jar`

Swagger

Swagger can be accessed here <http://localhost:8080/swagger-ui.html>

H2-Console

H2 Console can be accessed here (with creds -> admin/password) <http://localhost:8080/h2-console>

**Next Version Improvements**
- Logs should be written in log files as well
- Code can be distributed in different layers like validator, mapper etc.
- Additional configuration for environment specific, ports etc
