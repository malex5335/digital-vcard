package com.riagade.vcard.entities;

import lombok.*;

import static org.apache.commons.lang3.StringUtils.stripToEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String street;
    private String city;
    private String state;
    private String zip;
    private String country;

    public String asFieldString() {
        return ";;%s;%s;%s;%s;%s".formatted(
                stripToEmpty(getStreet()),
                stripToEmpty(getCity()),
                stripToEmpty(getState()),
                stripToEmpty(getZip()),
                stripToEmpty(getCountry()));
    }

    @AllArgsConstructor
    @Getter
    public enum AddressCategory {
        HOME("HOME"), WORK("WORK");
        private final String value;
    }
}
