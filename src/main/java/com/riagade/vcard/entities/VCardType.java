package com.riagade.vcard.entities;

import jakarta.annotation.Nonnull;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VCardType implements FieldProvider {
    public static final DateTimeFormatter REV_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    @Nonnull
    private ContactType contactType;
    private LocalDateTime lastUpdated;

    @Override
    public Map<String, String> getFields() {
        setDefaultIfNull(this::getLastUpdated, this::setLastUpdated, LocalDateTime.now());
        return Map.of("REV:", getLastUpdated().format(REV_DATE_FORMAT));
    }

    @AllArgsConstructor
    @Getter
    public enum ContactType {
        INDIVIDUAL("individual"), ORGANISATION("organisation"), GROUP("group"), TEAM("team");
        private final String value;
    }
}
