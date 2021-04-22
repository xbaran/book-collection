# Book Collection App

## Assignment

Create a simple RESTful API server for the book collection app. Each book has a title, description, and 1-n authors. Created API endpoints should allow search in collection and book management.

Implementation details:
- app should support standard CRUD ops 
- tools, framework, and database are up to you, but you have to write short why? behind your decision


## Project organization

### Module - schema

The first step was to create a schema for the API in OpenAPI v3 format. 
Then to generate server code from OpenAPI schema as a part of `mvn install`. Then to use it to implement business logic in `api` module.

### Module - api

Contains all business logic and unit tests. The tools and framework has been chosen as:

### Module - bom

It literally means `bill of materials` and it's best practice how to organize maven project to prevent dependency version clash.


### Module - samples

Collections of scripts and helpers for the demo purposes.  

## Frameworks, tools and databases

* Framework: Spring Book 2.4.4 - Used for configurations, DI, Tests, Transaction, API controller, AoP, web server  
* ORM: JPA2 with underlying Hibernate - Used to map entities to DB and generate initial schema.
* DB Connection pool: implicit Hikari
* WebServer: implicit Tomcat
* Logging: Slf4j facade with Logback
* Monitoring: None 
* Healthcheck: Spring Actuator
* Cors: Spring security, but its disabled for demo purposes
* ModelMapper: Map API and DB entities
* RSQL: Search language definition
* Lombok: Getter/Setters and Builder generator for beans

* H2 Database - used for unit test as embedded db
* mysql Database - used as external db (most popular db but missing some advanced features)
* postgresql Database - used as external db (best free db in my opinion with WAL, many extensions)

## Application build

### Build build

To build application jars with unit tests run:
```
mvn install
```

To build and push docker image `mbaran/book-collection-app:mysql-1.0-SNAPSHOT` for `mysql` run:

```
mvn clean install -P mysql
mvn -pl api docker:build -P mysql
mvn -pl api docker:push -P mysql
```

To build and push docker image `mbaran/book-collection-app:psql-1.0-SNAPSHOT` for `psql` run:

```
mvn clean install -P psql
mvn -pl api docker:build -P psql
mvn -pl api docker:push -P psql
```

## Application properties

###Profiles

 Only one of DB profile should be use, if none selected h2 profile is used.
 
 To select profiles add env property `SPRING_PROFILES_ACTIVE` for example `SPRING_PROFILES_ACTIVE=default,h2,swagger,init` will create application with h2 db and put test dataset during startup and also create swagger console. 

 #### *h2*
 Connects application with h2 embedded database. Used for tests, but can be used with application build also.
 #### *mysql*
 Connects application with external mysql database.
 
 | Env. Parameter | Default | Desc |
 | ------ | ------ | ------ |
 | SLIDO_MYSQL_HOST | localhost | Mysql host | 
 | SLIDO_MYSQL_PORT | 3306 | Mysql port | 
 | SLIDO_MYSQL_DATABASE | slido | Mysql database |
 | SLIDO_MYSQL_USER | slido | Mysql username |
 | SLIDO_MYSQL_PASSWORD | slido | Mysql password | 

 #### *psql*
 Connects application with external postgresql database.
 
 | Env. parameter | Default | Desc |
 | ------ | ------ | ------ |
 | SLIDO_PSQL_HOST | localhost | Psql host | 
 | SLIDO_PSQL_PORT | 5432 | Psql port | 
 | SLIDO_PSQL_DATABASE | slido | Psql database |
 | SLIDO_PSQL_USER | slido | Psql username |
 | SLIDO_PSQL_PASSWORD | slido | Psql password | 
 
  #### *swagger*
  
  Enables swagger console on `/swagger-ui.html` on localhost it's `http://localhost:8080/swagger-ui.html`

  #### *init*
  
  Initialize database with dummy data.
  
  #### *default*
    
  Defaut configuration.
     
## Run application in docker

Precondition is to have `docker` cli and docker deamon.

Run mysql database:

```docker run -d --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_USER=slido -e MYSQL_PASSWORD=slido -e MYSQL_DATABASE=slido  mysql:5.6```

Run psql database:

```docker run -d --name psql -p 5432:5432 -e POSTGRES_USER=slido -e POSTGRES_PASSWORD=slido -e POSTGRES_DB=slido  postgres:11```

Run application with embedded h2:

```docker run -it --rm --name book-collection-h2 -e SPRING_PROFILES_ACTIVE=default,h2,swagger,init -p 8080:8080 mbaran/book-collection-app:h2-1.0-SNAPSHOT```

Run application with mysql:

```docker run -it --rm --name book-collection-mysql -e SPRING_PROFILES_ACTIVE=default,mysql,swagger,init --link mysql:mysql -e SLIDO_MYSQL_HOST=mysql -p 8080:8080 mbaran/book-collection-app:mysql-1.0-SNAPSHOT```

Run application with psql:

```docker run -it --rm --name book-collection-psql -e SPRING_PROFILES_ACTIVE=default,psql,swagger,init --link psql:psql -e SLIDO_PSQL_HOST=psql -p 8080:8080 mbaran/book-collection-app:psql-1.0-SNAPSHOT```

Cleanup dbs

```
docker rm -f mysql
docker rm -f psql
```

## Run application in kubernetes

Precondition is to have `kubectl` and `k3d` cli and docker deamon.

Create k8s cluster with 2 workers and loadbalancer on 8080

```$xslt
k3d cluster create demo --api-port 6550 -p "8080:80@loadbalancer" --agents 2
```

Run api on `http://localhost:8080/h2/swagger-ui.html`

```$xslt
kubectl create namespace h2
kubectl apply -n h2 -f samples/k8s/app-deployment-h2.yaml
kubectl get pod -n h2 --watch #Wait while containers are running
```

Run api on `http://localhost:8080/mysql/swagger-ui.html`

```$xslt
kubectl create namespace mysql
kubectl apply -n mysql -f samples/k8s/app-deployment-mysql.yaml
kubectl get pod -n mysql --watch #Wait while containers are running
```

Run api on `http://localhost:8080/psql/swagger-ui.html`

```$xslt
kubectl create namespace psql
kubectl apply -n psql -f samples/k8s/app-deployment-psql.yaml
kubectl get pod -n psql --watch #Wait while containers are running
```

Clean up
```$xslt
kubectl delete namespace h2
kubectl delete namespace mysql
kubectl delete namespace psql
```

or 

```$xslt
k3d cluster delete demo
```





