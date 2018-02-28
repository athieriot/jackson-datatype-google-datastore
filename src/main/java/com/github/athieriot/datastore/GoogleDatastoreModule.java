package com.github.athieriot.datastore;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.cloud.datastore.FullEntity;

public class GoogleDatastoreModule extends SimpleModule
{
    private static final long serialVersionUID = 1;

    private final static String NAME = "GoogleDatastoreModule";

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public GoogleDatastoreModule()
    {
        super(NAME, Version.unknownVersion());
        addDeserializer(FullEntity.class, GoogleDatastoreEntityDeserializer.instance);
        addSerializer(GoogleDatastoreEntitySerializer.instance);
    }
}