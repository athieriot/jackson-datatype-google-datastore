package com.github.athieriot.datastore;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.ValueType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;

public class GoogleDatastoreEntitySerializer extends StdSerializer<FullEntity>
{
    private static final long serialVersionUID = 1L;

    public final static GoogleDatastoreEntitySerializer instance = new GoogleDatastoreEntitySerializer();

    private GoogleDatastoreEntitySerializer()
    {
        super(FullEntity.class);
    }

    @Override
    public void serialize(FullEntity value, JsonGenerator g, SerializerProvider provider)
        throws IOException
    {
        g.writeStartObject(value);
        serializeContents(value, g, provider);
        g.writeEndObject();
    }

    @Override
    public void serializeWithType(FullEntity value, JsonGenerator g, SerializerProvider provider,
            TypeSerializer typeSer) throws IOException
    {
        g.setCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        serializeContents(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);

    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint)
        throws JsonMappingException
    {
        return createSchemaNode("object", true);
    }

    protected void serializeContents(FullEntity value, JsonGenerator g, SerializerProvider provider)
        throws IOException
    {
        Iterator it = value.getNames().iterator();

        while (it.hasNext()) {
            String key = (String) it.next();

            if (value.isNull(key)) {
                if (provider.isEnabled(SerializationFeature.WRITE_NULL_MAP_VALUES)) {
                    g.writeNullField(key);
                }
                continue;
            }
            g.writeFieldName(key);

            ValueType type = value.getValue(key).getType();
            if (type == ValueType.ENTITY) {
                serialize(value.getEntity(key), g, provider);
            } else if (type == ValueType.LIST) {
                serializeList(value, g, provider, key);
            }
            else if (type == ValueType.STRING) {
                g.writeString(value.getString(key));
            } else if (type == ValueType.LONG) {
                g.writeNumber(value.getLong(key));
            } else if (type == ValueType.BOOLEAN) {
                g.writeBoolean(value.getBoolean(key));
            } else if (type == ValueType.DOUBLE) {
                g.writeNumber(value.getDouble(key));
            } else {
                g.writeString(value.getValue(key).toString());
            }
        }
    }

    private void serializeList(FullEntity value, JsonGenerator g, SerializerProvider provider, String key) throws IOException {
        g.writeStartArray();

        for (Object o : value.getList(key)) {
            if (o instanceof EntityValue) {
                serialize(((EntityValue) o).get(), g, provider);
            } else {
                provider.defaultSerializeValue(o, g);
            }
        }

        g.writeEndArray();
    }
}
