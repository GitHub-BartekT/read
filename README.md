# $${\color{red}{do-read}}$$
The **do-read** app helps children learn to read by playing short, 
focused reading sessions before watching cartoons. 
This method is inspired by Glenn and Janet Doman’s book 
and aims to reduce the time parents spend preparing learning materials while keeping children engaged.

## How it works
### Problem
In the book *How to Teach Your Baby to Read*, the Domans describe a method to teach toddlers and preschoolers to read. While the daily learning sessions are short, 
the preparation takes a lot of effort, leading many parents to abandon the method over time.
### Solution
Most children regularly watch cartoons. **do-read** integrates reading lessons into their screen time, where parents can play brief reading sessions that align with the Domans’ teaching methods.

## Screenshots + Live Demo
### WWW
🌐[Ro to the website!](http://ec2-3-64-60-246.eu-central-1.compute.amazonaws.com:8000/)🌐

### Live demo
📺[Watch the demo on YouTube](https://youtu.be/iQrmxAIZE3I)📺
### Login page
![login-page.png](documentation%2Freadme%2Flogin-page.png)
### Landing page
![landing-page.png](documentation%2Freadme%2Flanding-page.png)
### Session page
![session.png](documentation%2Freadme%2Fsession.png)
## What Has Been Accomplished💡
### Architecture ✅
The app follows a **clean, modular hexagonal architecture**:
* REST API endpoints.
* Some HTML files are static, while others are MVC templates.
* Security integration is based on a separate repository.
#### Modules:
#### Business Modules
* Module:
  * Manages individual modules, which consist of sets of sentences.
  * Responsible for:
    * Setting the current lesson.
    * Defining the number of sentences displayed per session.
    * Storing module-related data, such as the number of daily sessions, newly learned sentences, and sorting data by session number.
* Sentence:
  * Implements business logic related to managing sentences within user modules.
  * Main tasks:
    * Storing: Stores sentences in the context of specific user modules.
    * Sorting: Sorts sentences according to a predefined order (e.g., ordinalNumber).
    * Modifying: Allows modifying the content of sentences and their attributes.
    * Deleting: Manages the deletion of sentences assigned to user modules.
* Session:
  * Combines individual modules into learning sessions.
  * A session represents a set of actions performed within a single module, such as reviewing, revisiting, and learning new sentences.
  * Manages user progress within lessons, for example, controlling which sentences have already been learned.
#### Infrastructural Modules
* Details: [my security git-hub repo](https://github.com/GitHub-BartekT/SpringBoot_Security_Module)
### Database✅
* PostgreSQL database.
* Migrations managed by **Flyway**.
* Designed with an Entity Relationship Diagram (ERD) for reference.
#### ERD
![schema.png](documentation%2Freadme%2Fschema.png)
### Technologies
#### Backend
* **Java 17, Maven, Spring Boot** 
* **Postegre SQL, Hibernate,FlyWay**
* **Spring Security** with email login confirmation, JWT access, and refresh tokens.
  * Details: [my security git-hub repo](https://github.com/GitHub-BartekT/SpringBoot_Security_Module)
#### Frontend
* HTML, CSS, JavaScript
#### Version Control
* Git / GitHub
### Version 0.1 - SNAPSHOT
* **Basic functionality** - ✅
  1. running session
  2. increase session meter, after each session
* **Security**✅
* **Predefined session** for each new user✅
* **Big red text** on a white background during sessions✅
* Easy text updates during sessions✅
* Basic words database✅

## Pending features
### Version 1.0
1. Module CRUD✅
2. Sentence CRUD✅
3. Backend for statistics
4. Launch🚀
### Version 1.1
1. Add password management
2. Redraw pages:
   1. about
   2. account
   3. landing page
3. Auto login
### Version 2.0
1. Session CRUD - allowing multiple modules per session.
2. Statistic view
### Version 2.1
1. Admin View
### Version 3.0
1. PictureModule CRUD for "bits of intelligence" feature.

## Running Application:
To start the application:

```mvn spring-boot:run```

Access via [localhost:8080](http://localhost:8080)

## Running Tests:

* Unit Tests:

```mvn test```

* Integration Tests:

```mvn failsafe:integration-test```

* Unit + Integration Tests:

```mvn verify```

## Documentation
For reference: *How to Teach Your Baby to Read* by Glenn Doman and Janet Doman.

## Contact:
https://www.linkedin.com/in/bartlomiejtucholski/
https://iseebugs.pl/

