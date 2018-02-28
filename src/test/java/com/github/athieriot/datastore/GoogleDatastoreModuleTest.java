package com.github.athieriot.datastore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GoogleDatastoreModuleTest {

	private static final String JSON = "{\n" +
			"  \"address\": {\n" +
			"    \"city\": \"New York\",\n" +
			"    \"postalCode\": \"10021-3100\",\n" +
			"    \"state\": \"NY\",\n" +
			"    \"streetAddress\": \"21 2nd Street\"\n" +
			"  },\n" +
			"  \"age\": 27,\n" +
			"  \"children\": [],\n" +
			"  \"firstName\": \"John\",\n" +
			"  \"isAlive\": true,\n" +
			"  \"lastName\": \"Smith\",\n" +
			"  \"phoneNumbers\": [\n" +
			"    {\n" +
			"      \"number\": \"212 555-1234\",\n" +
			"      \"type\": \"home\"\n" +
			"    },\n" +
			"    {\n" +
			"      \"number\": \"646 555-4567\",\n" +
			"      \"type\": \"office\"\n" +
			"    },\n" +
			"    {\n" +
			"      \"number\": \"123 456-7890\",\n" +
			"      \"type\": \"mobile\"\n" +
			"    }\n" +
			"  ],\n" +
			"  \"spouse\": null\n" +
			"}";

	private static final FullEntity<IncompleteKey> ENTITY;

	static {{
		FullEntity<IncompleteKey> address = Entity.newBuilder()
				.set("streetAddress", "21 2nd Street")
				.set("city", "New York")
				.set("state", "NY")
				.set("postalCode", "10021-3100")
				.build();

		FullEntity<IncompleteKey> phoneNumber1 = Entity.newBuilder()
				.set("type", "home")
				.set("number", "212 555-1234")
				.build();

		FullEntity<IncompleteKey> phoneNumber2 = Entity.newBuilder()
				.set("type", "office")
				.set("number", "646 555-4567")
				.build();

		FullEntity<IncompleteKey> phoneNumber3 = Entity.newBuilder()
				.set("type", "mobile")
				.set("number", "123 456-7890")
				.build();

		ENTITY = Entity.newBuilder()
				.set("firstName", "John")
				.set("lastName", "Smith")
				.set("isAlive", true)
				.set("age", 27L)
				.set("address", address)
				.set("phoneNumbers", phoneNumber1, phoneNumber2, phoneNumber3)
				.set("children", new ArrayList<>())
				.setNull("spouse")
				.build();
	}}

	private ObjectMapper mapper;

	@Before
	public void setup() {
		mapper = new ObjectMapper();
		mapper.registerModule(new GoogleDatastoreModule());
	}

	@Test
	public void testDeserializeEntity() throws IOException {
		FullEntity result = mapper.readValue(JSON, FullEntity.class);

		assertThat(result.getString("firstName"), is(ENTITY.getString("firstName")));
		assertThat(result.getString("lastName"), is(ENTITY.getString("lastName")));
		assertThat(result.getLong("age"), is(ENTITY.getLong("age")));
		assertThat(result.getList("children").isEmpty(), is(true));
		assertThat(result.getBoolean("isAlive"), is(ENTITY.getBoolean("isAlive")));
		assertThat(result.getString("spouse"), is(nullValue()));
		assertThat(result.getEntity("address"), is(ENTITY.getEntity("address")));
		assertThat(result.getList("phoneNumbers"), is(ENTITY.getList("phoneNumbers")));
	}

	@Test
	public void testSerializeEntity() throws IOException {

		assertThat(mapper.readTree(mapper.writeValueAsString(ENTITY)), is(mapper.readTree(JSON)));
	}
}
