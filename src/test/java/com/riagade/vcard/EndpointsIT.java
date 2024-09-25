package com.riagade.vcard;

import com.riagade.vcard.entities.*;
import com.riagade.vcard.entities.Contact.ContactCategory;
import com.riagade.vcard.entities.Contact.Email;
import com.riagade.vcard.entities.Contact.Telephone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.riagade.vcard.entities.VCardType.REV_DATE_FORMAT;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EndpointsIT {
    @Test
    void vcard() {
        // Given
        var updateDate = LocalDateTime.now();
        var vcardInfo = VCardInformation.builder()
                .vcardType(VCardType.builder()
                        .lastUpdated(updateDate)
                        .contactType(VCardType.ContactType.INDIVIDUAL)
                        .build())
                .personalInformation(Person.builder()
                        .title(Person.Title.NONE)
                        .firstname("Jon")
                        .lastname("Doe")
                        .gender(Person.Gender.MALE)
                        .birthdate(LocalDate.of(2012, 3, 14))
                        .birthplace("Berlin, Germany")
                        .build())
                .workInformation(WorkPerson.builder()
                        .company("Lightning Portal Co.")
                        .department("SW")
                        .team("Consulting")
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
        var expectedResponse = """
                BEGIN:VCARD
                VERSION:4.0
                TITLE:Senior Software Engineer
                BDAY:2012-03-14
                N:Doe;Jon;;;
                ROLE:Consulting
                URL:https://www.google.com
                EMAIL;TYPE=WORK;VALUE=uri:mailto:jon.doe@company.uk
                REV:%s
                FN:Jon Doe
                ORG:Lightning Portal Co.;SW;Consulting
                BIRTHPLACE:Berlin, Germany
                TEL;TYPE=HOME:+491234567890
                TEL;TYPE=WORK:+49030123456
                EMAIL;TYPE=HOME;VALUE=uri:mailto:mind.body@world.com
                END:VCARD
                """.formatted(updateDate.format(REV_DATE_FORMAT));
        // When
        var actualResponse = given()
                .contentType(APPLICATION_JSON.toString())
                .body(vcardInfo)
                .when()
                .post("/vcard")
                .then()
                .extract().body().asString();
        // Then
        assertEquals(expectedResponse, actualResponse);
    }
}