package com.riagade.vcard.entities;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.stripToNull;

@Data
@Builder
public class VCardInformation {
    @Nonnull
    private VCardType vcardType;
    @Nonnull
    private Person personalInformation;
    private WorkPerson workInformation;
    @Nonnull
    private Contact contactInformation;
    private SocialMedia socialMedia;

    @Override
    public String toString() {
        return """
                BEGIN:VCARD
                VERSION:4.0
                %s
                END:VCARD
                """.formatted(getFields().entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> entry.getKey() + entry.getValue())
                .collect(Collectors.joining("\n")));
    }

    private Map<String, String> getFields() {
        return Stream.of(vcardType, personalInformation, workInformation, contactInformation, socialMedia)
                .filter(Objects::nonNull)
                .map(FieldProvider::getFields)
                .flatMap(map -> map.entrySet().stream())
                .filter(entry -> stripToNull(entry.getValue()) != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
