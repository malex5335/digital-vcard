package com.riagade.vcard.entities;

import com.riagade.vcard.entities.Contact.ContactCategory;
import com.riagade.vcard.entities.Contact.Email;
import com.riagade.vcard.entities.Contact.Telephone;
import com.riagade.vcard.entities.Person.Gender;
import com.riagade.vcard.entities.Person.Title;
import com.riagade.vcard.entities.VCardType.ContactType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.riagade.vcard.entities.VCardType.REV_DATE_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VCardInformationTest {
    @Test
    void testVCardInformation() {
        // Given
        var updateDate = LocalDateTime.now();
        var vcardInfo = VCardInformation.builder()
                .vcardType(VCardType.builder()
                        .lastUpdated(updateDate)
                        .contactType(ContactType.INDIVIDUAL)
                        .build())
                .personalInformation(Person.builder()
                        .title(Title.NONE)
                        .firstname("Jon")
                        .lastname("Doe")
                        .gender(Gender.MALE)
                        .birthdate(LocalDate.of(2012, 3, 14))
                        .birthplace("Berlin, Germany")
                        .build())
                .workInformation(WorkPerson.builder()
                        .company("Lightning Portal Co.")
                        .department("Department")
                        .team("SW")
                        .jobTitle("Senior Software Engineer")
                        .build())
                .contactInformation(Contact.builder()
                        .telephones(List.of(
                                new Telephone(ContactCategory.HOME, "+491234567890"),
                                new Telephone(ContactCategory.WORK, "+49030123456")))
                        .emails(List.of(
                                new Email(ContactCategory.HOME, "mind.body@world.com"),
                                new Email(ContactCategory.WORK, "jon.doe@company.uk")))
                        .build())
                .socialMedia(SocialMedia.builder()
                        .website("https://www.google.com")
                        .build())
                .build();

        // When
        var vcard = vcardInfo.toString();

        // Then
        assertEquals("""
                BEGIN:VCARD
                VERSION:4.0
                BDAY:2012-03-14
                TITLE:Senior Software Engineer
                N:Doe;Jon;;;
                URL:https://www.google.com
                EMAIL;TYPE=WORK;VALUE=uri:mailto:jon.doe@company.uk
                REV:%s
                FN:Jon Doe
                ORG:Lightning Portal Co.;Department;SW
                TEL;TYPE=HOME:+491234567890
                BIRTHPLACE:Berlin, Germany
                TEL;TYPE=WORK:+49030123456
                GENDER:M
                EMAIL;TYPE=HOME;VALUE=uri:mailto:mind.body@world.com
                END:VCARD
                """.formatted(updateDate.format(REV_DATE_FORMAT)), vcard);
    }
}