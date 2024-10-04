# $${\color{red}{do-read}}$$
Program helps children learn to read.

## How it works
### Problem
In book “How to teach your baby to read” by Glenn and Janet Doman is described method of teaching toddlers and preschool children reading.
Time of everyday learning is very short (less than a minute), but preparing and organizing necessary materials takes a lot of time and energy. After a few weeks many parents give up because of lack of time.
### Solution
Most of the children watch cartoons. Program is compatible with the learning method of reading. In time when child is complete focussing on a screen parents can play sessions before cartoons.
## Screenshots
### Login page
![login-page.png](documentation%2Freadme%2Flogin-page.png)
### Landing page
![landing-page.png](documentation%2Freadme%2Flanding-page.png)
### Session page
![session.png](documentation%2Freadme%2Fsession.png)
## What Has Been Accomplished💡
Basic functionality in Java with Spring Boot and Security.
### Architecture ✅
Clean, modular hexagonal architecture. Uses my existing security repository. REST API endpoints. Frontend and emails are created as html files. Some of them are static files, some are MVC templates.
### Database✅
Database setup using PostegreSQL with migrations managed by Flyway. Entity Relationship Diagram (ERD) designed and available for reference.
#### Entity Relationship Diagram
![schema.png](documentation%2Freadme%2Fschema.png)
### Technologies
1.  Backend
- Environment - Intellij IDEA
    - Java 17
    - Maven
    - Spring Boot
- Databases
    - Postegre SQL
    - Hibernate
    - FlyWay
- Security
    - Email login confirmation
    - JWT - access and refresh
    - more information ->  [my security git-hub repo](https://github.com/GitHub-BartekT/SpringBoot_Security_Module)
4.  Frontend
- HTML
- CSS
- JavaScript
5.  Version Control
- Git / GitHub
### Version 1.0 - SNAPSHOT
1. Basic functionality - ✅
  1. running session
  2. increase session meter, after each session
2. Security✅
3. Create predefined session for each new user✅
4. Showing big red text on white background✅
5. Quick and easy changing texts during session✅
6. Database of basic words✅

## Pending features
### Version 1.1
1. Module CRUD
2. Add statistics backend
### Version 1.2
1. Session CRUD - merging few modules into one session
2. Kick off🚀
### Version 2.0
1. Statistic view
2. Admin View
### Version 2.1
1. PictureModule CRUD - bits of intelligence

## Running Application:

```mvn spring-boot:run```

Access via [localhost:8080](http://localhost:8080)

## Documentation
1. How to teach your baby to read. Glenn Doman, Janet Doman
