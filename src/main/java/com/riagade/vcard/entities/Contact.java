package com.riagade.vcard.entities;

import jakarta.annotation.Nonnull;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact implements FieldProvider {
    private List<Email> emails;
    private List<Telephone> telephones;

    @Override
    public Map<String, String> getFields() {
        setDefaultIfNull(this::getEmails, this::setEmails, emptyList());
        var emailStream = getEmails().stream()
                .map(email -> Map.entry("EMAIL;TYPE=%s;VALUE=uri:mailto:".formatted(
                        email.getCategory().getValue()), email.getEmail()));

        setDefaultIfNull(this::getTelephones, this::setTelephones, emptyList());
        var telephoneStream = getTelephones().stream()
                .map(telephone -> Map.entry("TEL;TYPE=%s:".formatted(telephone.getCategory().getValue()), telephone.getNumber()));

        return Stream.concat(emailStream, telephoneStream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Getter
    @AllArgsConstructor
    public enum ContactCategory {
        HOME("HOME"), WORK("WORK");
        private final String value;
    }

    @Data
    @AllArgsConstructor
    public static class Email {
        @Nonnull
        private ContactCategory category;
        @Nonnull
        private String email;
    }

    @Data
    @AllArgsConstructor
    public static class Telephone {
        @Nonnull
        private ContactCategory category;
        @Nonnull
        private String number;
    }
}
