# Voting Manager Application

- [Challenge]()
- [About it]()
- [Getting Started]()
- [API Documentation]()
- [Next Features and Improvements]()
---

## Challenge

#### Objective
In cooperatives, each associate has a vote and all decisions are made by voting within 
assemblies. From that, you need to create a back-end solution to manage these voting 
sessions. The solution must be able to run on cloud and provide the follow functionalities 
within a REST API:   

- Register a new agenda;
- Open a voting session with an agenda (the session must stay open for a time specified on the open call or 1 minute by default);
- Accept votes from associates (votes are only YES or NO. Each associate has a unique ID and can only vote one time per agenda);
- Count votes and return the result;

For exercise purposes, security can be abstracted and any call to the API can be considered authorized. The language, frameworks and libraries choice is free (since it doesn't infringe rights of use). 
It's important to persist agendas and votes. They shouldn't be lost on application restart.

#### Bonus Tasks
Bonus tasks aren't mandatory but allow us to evaluate other knowledge you may have.
We always suggest each candidate to ponder and present as far as he/she can do, considering his/her knowledge level and delivery quality.

##### Bonus Task 1 - External system integration
Integrate with a system that verify if a candidate can vote by CPF.
 
- GET https://user-info.herokuapp.com/users/{cpf}

If CPF is invalid, API will return HTTP Status 404 (Not found). You can use a CPF generator to get valid CPFs;  
If CPF is valid, API will return if the candidate can (ABLE_TO_VOTE) or can't (UNABLE_TO_VOTE) execute the operation;  

##### Bonus Task 2 - Messaging and queue
Information classification: Internal Use  
The voting result must be informed to the remaining platform, this should be done preferably via messaging. When a voting session closes, post a message with the voting result.  

##### Bonus Task 3 - Performance
Imagine your application can be used in scenarios where hundreds of thousands votes exists. 
It must behave performative in these scenarios.  
Performance tests are a great way to grant and observe how your application behaves.

##### Bonut Task 4 - API Version
How would you version your application API? Which strategy will you use?  

## About it
WIP

## Getting Started

To run this application you will need:
- [Docker](https://docs.docker.com/get-docker/);
- [Docker Compose](https://docs.docker.com/compose/install/);

Clone this repo and in its folder run the following command:  
`docker-compose up`

## API Documentation
Once your application is running, you'll be able to see all available APIs through Swagger. You can access it with the following link:  
`http://localhost:8080/swagger-ui.html` 

## Next Features and Improvements
WIP



