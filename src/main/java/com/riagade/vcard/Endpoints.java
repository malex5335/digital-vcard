package com.riagade.vcard;

import com.riagade.vcard.entities.VCardInformation;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class Endpoints {
    private final QrGenerator qrGenerator;
    private final GoogleTokenCreator googleTokenCreator;

    @PostMapping("/vcard")
    public String vcard(@RequestBody VCardInformation vCardInformation) {
        return vCardInformation.toString();
    }

    @PostMapping("/vcard/qr")
    @SneakyThrows
    public ResponseEntity<ByteArrayResource> vcardQr(@Validated @RequestBody VCardInformation vCardInformation,
                                                     @RequestParam(defaultValue = "500", required = false) int size) {
        var person = vCardInformation.getPersonalInformation();
        var filename = "%s_%s.vcard.png".formatted(person.getFirstname(), person.getLastname());
        var qrBytes = qrGenerator.generateVcard(vCardInformation, size);
        var resource = new ByteArrayResource(qrBytes);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(filename)
                                .build().toString())
                .body(resource);
    }

    @PostMapping("/google/token")
    public String googleToken(@Validated @RequestBody VCardInformation vCardInformation) {
        return googleTokenCreator.createGoogleLink(vCardInformation);
    }

    @PostMapping("/apple/file")
    public byte[] appleFile(@Validated @RequestBody VCardInformation vCardInformation) {
        return new byte[0];
    }
}
