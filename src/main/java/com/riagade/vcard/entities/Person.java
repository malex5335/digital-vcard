package com.riagade.vcard.entities;

import com.riagade.vcard.entities.Address.AddressCategory;
import jakarta.annotation.Nonnull;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.stripToEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person implements FieldProvider {
    public static final DateTimeFormatter BIRTHDAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Nonnull
    private String firstname;
    @Nonnull
    private String lastname;
    private Title title;
    private Gender gender;
    private LocalDate birthdate;
    private String birthplace;
    private Address address;

    @Override
    public Map<String, String> getFields() {
        setDefaultIfNull(this::getTitle, this::setTitle, Title.NONE);
        var fields = new HashMap<String, String>();
        var title = Optional.ofNullable(getTitle())
                .map(Title::getValue)
                .orElse("");
        fields.put("N:", "%s;%s;;%s;".formatted(
                stripToEmpty(getLastname()),
                stripToEmpty(getFirstname()),
                title
        ));
        fields.put("FN:", "%s %s %s".formatted(
                title,
                stripToEmpty(getFirstname()),
                stripToEmpty(getLastname())
        ).trim());
        fields.put("GENDER:", Optional.ofNullable(getGender())
                .map(Gender::getValue)
                .orElse(null));
        fields.put("BDAY:", Optional.ofNullable(getBirthdate())
                .map(date -> date.format(BIRTHDAY_FORMAT))
                .orElse(null));
        fields.put("BIRTHPLACE:", getBirthplace());
        if (getAddress() != null) {
            fields.put("ADR;TYPE=%s:".formatted(AddressCategory.HOME.getValue()), getAddress().asFieldString());
        }
        return fields;
    }

    @AllArgsConstructor
    @Getter
    public enum Gender {
        MALE("M"), FEMALE("F"), OTHER("O"), INTERSEX("intersex");
        private final String value;
    }

    @AllArgsConstructor
    @Getter
    public enum Title {
        NONE(""), DR("Dr."), PROF("Prof.");
        private final String value;
    }
}
