package com.seele2.encrypt.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class EncryptTable implements Serializable {

    private String name;

    private String alias;

    private Set<String> fields;

    public Set<String> getFields() {
        if (Objects.isNull(fields)) {
            fields = new HashSet<>();
        }
        return fields;
    }
}
