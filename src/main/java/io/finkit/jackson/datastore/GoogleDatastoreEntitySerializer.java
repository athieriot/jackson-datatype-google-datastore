package io.finkit.jackson.datastore;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.Value;

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

//    @Override // since 2.6
//    public boolean isEmpty(SerializerProvider provider, Entity value) {
//        return (value == null) || value.length() == 0;
//    }

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

            Value v = value.getValue(key);
            v.getType()

//            Object ob = value.opt(key);
//            g.writeFieldName(key);
//            Class<?> cls = ob.getClass();
//            if (cls == JSONObject.class) {
//                serialize((JSONObject) ob, g, provider);
//            } else if (cls == JSONArray.class) {
//                JSONArraySerializer.instance.serialize((JSONArray) ob, g, provider);
//            } else  if (cls == String.class) {
//                g.writeString((String) ob);
//            } else  if (cls == Integer.class) {
//                g.writeNumber(((Integer) ob).intValue());
//            } else  if (cls == Long.class) {
//                g.writeNumber(((Long) ob).longValue());
//            } else  if (cls == Boolean.class) {
//                g.writeBoolean(((Boolean) ob).booleanValue());
//            } else  if (cls == Double.class) {
//                g.writeNumber(((Double) ob).doubleValue());
//            } else if (JSONObject.class.isAssignableFrom(cls)) { // sub-class
//                serialize((JSONObject) ob, g, provider);
//            } else if (JSONArray.class.isAssignableFrom(cls)) { // sub-class
//                JSONArraySerializer.instance.serialize((JSONArray) ob, g, provider);
//            } else {
//                provider.defaultSerializeValue(ob, g);
//            }
        }
    }
}
