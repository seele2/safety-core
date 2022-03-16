package com.seele2.encrypt.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.seele2.encrypt.annotation.Desensitize;

import java.io.IOException;
import java.util.Objects;

public class DesensitizeSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private Desensitize desensitize;

    public DesensitizeSerializer() {}

    public DesensitizeSerializer(Desensitize desensitize) {this.desensitize = desensitize;}

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (Objects.isNull(beanProperty)) return serializerProvider.findNullValueSerializer(null);
        Desensitize annotation = beanProperty.getAnnotation(Desensitize.class);
        if (Objects.isNull(annotation)) {
            annotation = beanProperty.getContextAnnotation(Desensitize.class);
        }
        if (Objects.isNull(annotation)) {
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return new DesensitizeSerializer(annotation);
    }

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (Objects.isNull(desensitize)) {
            jsonGenerator.writeString(s);
        }
        else {
            jsonGenerator.writeString(desensitize.type().getFunc().apply(s));
        }
    }
}
