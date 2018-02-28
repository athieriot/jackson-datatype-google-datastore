package com.github.athieriot.datastore;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.NullValue;

import java.io.IOException;

public class GoogleDatastoreEntityDeserializer extends StdDeserializer<FullEntity>
{
    private static final long serialVersionUID = 1L;

    public static final GoogleDatastoreEntityDeserializer instance = new GoogleDatastoreEntityDeserializer();

    private GoogleDatastoreEntityDeserializer()
    {
        super(Entity.class);
    }

    @Override
    public FullEntity deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {

        FullEntity.Builder<IncompleteKey> entity = Entity.newBuilder();
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        }
        for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
            String fieldName = p.getCurrentName();
            t = p.nextToken();
            switch (t) {
                case START_ARRAY:
                    entity.set(fieldName, deserializeArray(p, ctxt));
                    continue;
                case START_OBJECT:
                    entity.set(fieldName, deserialize(p, ctxt));
                    continue;
                case VALUE_STRING:
                    entity.set(fieldName, p.getText());
                    continue;
                case VALUE_NULL:
                    entity.setNull(fieldName);
                    continue;
                case VALUE_TRUE:
                    entity.set(fieldName, true);
                    continue;
                case VALUE_FALSE:
                    entity.set(fieldName, false);
                    continue;
                case VALUE_NUMBER_INT:
                    entity.set(fieldName, p.getIntValue());
                    continue;
                case VALUE_NUMBER_FLOAT:
                    entity.set(fieldName, p.getDoubleValue());
                    continue;
                default:
            }
            return (FullEntity) ctxt.handleUnexpectedToken(FullEntity.class, p);
        }
        return entity.build();
    }

    private ListValue deserializeArray(JsonParser p, DeserializationContext ctxt)
            throws IOException
    {
        ListValue.Builder list = ListValue.newBuilder();
        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
            switch (t) {
                case START_ARRAY:
                    list.addValue(deserializeArray(p, ctxt));
                    continue;
                case START_OBJECT:
                    list.addValue(deserialize(p, ctxt));
                    continue;
                case VALUE_STRING:
                    list.addValue(p.getText());
                    continue;
                case VALUE_NULL:
                    list.addValue(new NullValue());
                    continue;
                case VALUE_TRUE:
                    list.addValue(true);
                    continue;
                case VALUE_FALSE:
                    list.addValue(false);
                    continue;
                case VALUE_NUMBER_INT:
                    list.addValue(p.getNumberValue().longValue());
                    continue;
                case VALUE_NUMBER_FLOAT:
                    list.addValue(p.getNumberValue().floatValue());
                    continue;
                case VALUE_EMBEDDED_OBJECT:
                    list.addValue(p.getEmbeddedObject().toString());
                    continue;
                default:
                    return (ListValue) ctxt.handleUnexpectedToken(handledType(), p);
            }
        }
        return list.build();
    }
}
