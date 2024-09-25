package com.riagade.vcard.entities;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface FieldProvider {
    Map<String, String> getFields();

    default <T, R> void setDefaultIfNull(Supplier<T> getter, Consumer<R> setter, R defaultValue) {
        if (getter.get() == null) {
            setter.accept(defaultValue);
        }
    }
}
