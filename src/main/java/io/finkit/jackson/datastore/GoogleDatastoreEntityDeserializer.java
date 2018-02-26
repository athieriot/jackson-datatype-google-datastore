package io.finkit.jackson.datastore;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.cloud.datastore.*;
import com.oracle.javafx.jmx.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class GoogleDatastoreEntityDeserializer extends StdDeserializer<FullEntity>
{
    private static final long serialVersionUID = 1L;

    public final static GoogleDatastoreEntityDeserializer instance = new GoogleDatastoreEntityDeserializer();

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
            try {
                switch (t) {
                case START_ARRAY:
//                    entity.set(fieldName, JSONArrayDeserializer.instance.deserialize(p, ctxt));
                    entity.set(fieldName, new ArrayList<EntityValue>());
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
//                case VALUE_EMBEDDED_OBJECT:
//                    entity.set(fieldName, p.getEmbeddedObject());
//                    continue;
                default:
                }
            } catch (JSONException e) {
                throw ctxt.instantiationException(handledType(), e);
            }
            return (FullEntity) ctxt.handleUnexpectedToken(FullEntity.class, p);
        }
        return entity.build();
    }
}
