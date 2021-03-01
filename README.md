# KYPO Elasticsearch Service
This project represents the back-end service for retrieving Elasticsearch documents.

## Content

1. Project modules
2. Build and Start the Project Using Docker

### 1. Project Modules
This project is divided into several directories:
* `rest`
  * Provides REST layer for communication with front-end and other microservices.
  * Based on HTTP REST without HATEOAS.
  * Documented with Swagger.
* `service`
    * Calls data layer for Elasticsearch queries and combining the results as necessary.
    * Manages transactions and Aspect Oriented Programming (AOP) mechanisms.
* `data`
  * Provides data layer of the application for obtaining data from Elasticsearch.
  * Uses RestHighLevelClient to call Elasticsearch API.
* `api`
  * Contains API (DTO classes) 
    * Localized Bean validations are set (messages are localized).
    * Annotations for Swagger documentation are included.
  * Map Entities to DTO classes and vice versa with MapStruct framework.

### 2. Build and Start the Project Using Docker

#### Prerequisities
Install the following technology:

Technology        | URL to Download
----------------- | ------------
Docker            | https://docs.docker.com/install/

#### 1. Preparation of Configuration Files
To build and run the project in docker it is necessary to prepare several configurations:
* Set the [OpenID Connect configuration](https://docs.crp.kypo.muni.cz/installation-guide/setting-up-oidc-provider/).
* Fill OIDC credentials gained from the previous step and set additional settings in the [kypo-elasticsearch-service.properties](https://gitlab.ics.muni.cz/muni-kypo-crp/backend-java/kypo-elasticsearch-service/-/blob/master/etc/kypo-elasticsearch-service.properties) file and save it.

#### 2. Build Docker Image
The root folder of the project contains a Dockerfile with commands to assemble a docker image.  To build an image run the following command:
```bash
sudo docker build --build-arg PROPRIETARY_REPO_URL={path to proprietary repo} -t {image name} .
```

e.g.:
```bash
sudo docker build --build-arg PROPRIETARY_REPO_URL=https://gitlab.ics.muni.cz/api/v4/projects/2358/packages/maven -t elasticsearch-service-image .
```

Dockefile contains several default arguments:
* PROJECT_ARTIFACT_ID=kypo-elasticsearch-service - the name of the project artifact.
* PROPRIETARY_REPO_URL=YOUR-PATH-TO-PROPRIETARY_REPO.

Those arguments can be overwritten during the build of the image, by adding the following option for each argument: 
```bash
--build-arg {name of argument}={value of argument} 
``` 

#### 3. Start the Project
Before you run a docker container, make sure that your ***OIDC Provider*** and [kypo2-user-and-group](https://gitlab.ics.muni.cz/muni-kypo-crp/backend-java/kypo2-user-and-group) service is running. To run a docker container, run the following command: 
```
sudo docker run --name {container name} --network host -it -p {port in host}:{port in container} {docker image}
```
e.g. with this command:
```
sudo docker run --name elasticsearch-service-container --network host -it -p 8085:8085 elasticsearch-service-image
```

Add the following option to use the custom property file: 
```
-v {path to your config file}:/app/etc/kypo-elasticsearch-service.properties
```
