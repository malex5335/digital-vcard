package com.riagade.vcard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.riagade.vcard.entities.VCardType.REV_DATE_FORMAT;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class EndpointsIT {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Test
    void vcard() {
        // Given
        var updateDate = LocalDateTime.now();
        var vcardInfo = """
                {
                	"vcardType": {
                		"lastUpdated": "%s",
                		"contactType": "INDIVIDUAL"
                	},
                	"personalInformation": {
                		"gender": "MALE",
                		"title": null,
                		"firstname": "Jon",
                		"lastname": "Doe",
                		"birthdate": "2012-03-14",
                		"birthplace": "Berlin, Germany"
                	},
                	"workInformation": {
                		"jobTitle": "Senior Software Engineer",
                		"company": "Lightning Portal Co.",
                		"department": "SW",
                		"team": "Consulting",
                		"role": "Software-Consultant"
                	},
                	"contactInformation": {
                		"telephones": [
                			{
                				"category": "HOME",
                				"number": "+491234567890"
                			},
                			{
                				"category": "WORK",
                				"number": "+49030123456"
                			}
                		],
                		"emails": [
                			{
                				"category": "HOME",
                				"email": "mind.body@world.com"
                			},
                			{
                				"category": "WORK",
                				"email": "jon.doe@company.uk"
                			}
                		]
                	},
                	"socialMedia": {
                		"website": "https://www.google.com"
                	}
                }
                """.formatted(updateDate.format(DATE_FORMAT));
        // When
        var actualResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(vcardInfo)
                .when()
                .post("http://localhost:8080/vcard")
                .then()
                .extract().body().asString();
        // Then
        var expectedResponse = """
                BEGIN:VCARD
                VERSION:4.0
                BDAY:2012-03-14
                TITLE:Senior Software Engineer
                N:Doe;Jon;;;
                ROLE:Software-Consultant
                URL:https://www.google.com
                EMAIL;TYPE=WORK;VALUE=uri:mailto:jon.doe@company.uk
                REV:%s
                FN:Jon Doe
                ORG:Lightning Portal Co.;SW;Consulting
                TEL;TYPE=HOME:+491234567890
                BIRTHPLACE:Berlin, Germany
                TEL;TYPE=WORK:+49030123456
                GENDER:M
                EMAIL;TYPE=HOME;VALUE=uri:mailto:mind.body@world.com
                END:VCARD
                """.formatted(updateDate.format(REV_DATE_FORMAT));
        assertEquals(expectedResponse, actualResponse);
    }
}