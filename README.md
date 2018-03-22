Project to build Jackson (http://jackson.codehaus.org) extension module (jar) to support datatypes of Google Datastore Client (see [https://cloud.google.com/datastore/docs/reference/libraries])

## Status

[![Build Status](https://travis-ci.org/FasterXML/jackson-datatype-json-org.svg)](https://travis-ci.org/athieriot/jackson-datatype-google-datastore)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.athieriot/jackson-datatype-google-datastore/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.athieriot/jackson-datatype-google-datastore/)
[![Coverage Status](https://coveralls.io/repos/github/athieriot/jackson-datatype-google-datastore/badge.svg?branch=master)](https://coveralls.io/github/athieriot/jackson-datatype-google-datastore?branch=master)

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.github.athieriot</groupId>
  <artifactId>jackson-datatype-google-datastore</artifactId>
  <version>0.0.1</version>
</dependency>
```

You will also need the Google Datastore Client in the classpath

```xml
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>google-cloud-datastore</artifactId>
  <version>1.16.0</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

To use the the Module in Jackson, simply register it with the ObjectMapper instance:

```java
// import com.github.athieriot.datastore.GoogleDatastoreModule;

ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new GoogleDatastoreModule());
```

### Data conversions

After registering the module, you can read and write JSON to/from an instance of FullEntity<IncompleteKey> similar to handling custom POJOs or standard JDK types:

```java
FullEntity result = mapper.readValue(JSON, FullEntity.class);; // read from a source
String json = mapper.writeValueAsString(ENTITY); // output as String
```

This entity will miss its key, so before storing it in Datastore:

```java
Datastore datastore = DatastoreOptions.getDefaultInstance();

Key key = datastore.newKeyFactory().setKind("task").newKey("sampletask1");
Entity entity = Entity.newBuilder(key, result).build();

datastore.put(entity);
```
