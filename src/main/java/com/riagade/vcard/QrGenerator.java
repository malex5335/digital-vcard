package com.riagade.vcard;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.riagade.vcard.entities.VCardInformation;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class QrGenerator {
    @SneakyThrows
    public byte[] generateVcard(VCardInformation vCardInformation, int size) {
        var qrCode = new MultiFormatWriter().encode(vCardInformation.toString(), BarcodeFormat.QR_CODE, size, size);
        var baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(qrCode, "png", baos);
        return baos.toByteArray();
    }
}
