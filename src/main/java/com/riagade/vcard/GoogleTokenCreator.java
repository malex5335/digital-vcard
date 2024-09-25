package com.riagade.vcard;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.walletobjects.Walletobjects;
import com.google.api.services.walletobjects.WalletobjectsScopes;
import com.google.api.services.walletobjects.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.riagade.vcard.entities.VCardInformation;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class GoogleTokenCreator {
    private static final String GOOGLE_URL = "https://pay.google.com/gp/v/save/%s";
    private Walletobjects walletService;
    private GoogleCredentials credentials;

    @Value("${application.name}")
    private String applicationName;
    @Value("${google.access.token}")
    private String accessToken;
    @Value("${google.origins.url}")
    private String originsUrl;
    @Value("${google.issuer.id}")
    private String issuerId;
    @Value("${google.vcard.suffix}")
    private String classSuffix;

    @PostConstruct
    public void init() {
        auth();
    }

    @SneakyThrows
    private void auth() {
        this.credentials = GoogleCredentials.create(new AccessToken(accessToken, null))
                .createScoped(List.of(WalletobjectsScopes.WALLET_OBJECT_ISSUER));
        credentials.refresh();
        walletService = new Walletobjects.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(applicationName)
                .build();
    }

    public String createGoogleLink(VCardInformation vcard) {
        var algorithm = Algorithm.RSA256(null, (RSAPrivateKey) ((ServiceAccountCredentials) credentials).getPrivateKey());
        var token = JWT.create()
                .withIssuer(((ServiceAccountCredentials) credentials).getClientEmail())
                .withClaim("aud", "google")
                .withClaim("origins", List.of(originsUrl))
                .withClaim("typ", "savetowallet")
                .withClaim("payload", Map.of(
                        "genericClasses", List.of(createWalletClass()),
                        "genericObjects", List.of(createWalletObject(vcard)))
                )
                .sign(algorithm);
        return GOOGLE_URL.formatted(token);
    }

    @SneakyThrows
    private GenericClass createWalletClass() {
        return walletService.genericclass()
                .insert(new GenericClass().setId(getClassKey()))
                .execute();
    }

    @SneakyThrows
    private GenericObject createWalletObject(VCardInformation vcard) {
        var person = vcard.getPersonalInformation();
        var headline = "%s %s %s".formatted(person.getTitle(), person.getFirstname(), person.getLastname()).trim();
        var walletObject = new GenericObject()
                .setId(createObjectKey())
                .setClassId(getClassKey())
                .setState("ACTIVE")
                .setCardTitle(new LocalizedString()
                        .setDefaultValue(new TranslatedString()
                                .setLanguage("de")
                                .setValue(headline)))
                .setBarcode(new Barcode()
                        .setKind("QR_CODE")
                        .setValue(vcard.toString()));
        return walletService.genericobject()
                .insert(walletObject)
                .execute();
    }

    private String getClassKey() {
        return String.format("%s.%s", issuerId, classSuffix);
    }

    private String createObjectKey() {
        return String.format("%s.%s", issuerId, UUID.randomUUID());
    }
}
