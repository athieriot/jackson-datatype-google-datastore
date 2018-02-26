package io.finkit.jackson.datastore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.datastore.*;
import org.junit.Test;

import java.io.IOException;

public class GoogleDatastoreModuleTests {

	private final static String json = "{\n" +
			"  \"firstName\": \"John\",\n" +
			"  \"lastName\": \"Smith\",\n" +
			"  \"isAlive\": true,\n" +
			"  \"age\": 27,\n" +
			"  \"address\": {\n" +
			"    \"streetAddress\": \"21 2nd Street\",\n" +
			"    \"city\": \"New York\",\n" +
			"    \"state\": \"NY\",\n" +
			"    \"postalCode\": \"10021-3100\"\n" +
			"  },\n" +
			"  \"phoneNumbers\": [\n" +
			"    {\n" +
			"      \"type\": \"home\",\n" +
			"      \"number\": \"212 555-1234\"\n" +
			"    },\n" +
			"    {\n" +
			"      \"type\": \"office\",\n" +
			"      \"number\": \"646 555-4567\"\n" +
			"    },\n" +
			"    {\n" +
			"      \"type\": \"mobile\",\n" +
			"      \"number\": \"123 456-7890\"\n" +
			"    }\n" +
			"  ],\n" +
			"  \"children\": [],\n" +
			"  \"spouse\": null\n" +
			"}";

	@Test
	public void contextLoads() throws IOException {
		// Instantiates a client
		Datastore datastore = DatastoreOptions.newBuilder().setProjectId("finkit-testing").setNamespace("finkit").build().getService();

		//TODO: How to know the kind if root entity?
		Key key = datastore.newKeyFactory().setKind("Task").newKey("sampletask1");

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new GoogleDatastoreModule());

		FullEntity task = mapper.readValue(json, FullEntity.class);

		Entity entity = Entity.newBuilder(key, task).build();

		datastore.put(entity);
	}
}
