package com.riagade.vcard.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.stripToEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialMedia implements FieldProvider {
    private String website;

    @Override
    public Map<String, String> getFields() {
        return Map.of("URL:", stripToEmpty(getWebsite()));
    }
}
