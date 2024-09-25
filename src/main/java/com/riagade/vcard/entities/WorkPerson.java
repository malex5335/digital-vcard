package com.riagade.vcard.entities;

import com.riagade.vcard.entities.Address.AddressCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.stripToEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkPerson implements FieldProvider {
    private String company;
    private String department;
    private String team;
    private String role;
    private String jobTitle;
    private Address address;

    @Override
    public Map<String, String> getFields() {
        var fields = new HashMap<String, String>();
        fields.put("ORG:", "%s;%s;%s".formatted(
                stripToEmpty(getCompany()),
                stripToEmpty(getDepartment()),
                stripToEmpty(getTeam())));
        fields.put("ROLE:", stripToEmpty(getRole()));
        fields.put("TITLE:", stripToEmpty(getJobTitle()));
        if (getAddress() != null) {
            fields.put("ADR;TYPE=%s:".formatted(AddressCategory.WORK.getValue()), getAddress().asFieldString());
        }
        return fields;
    }
}
