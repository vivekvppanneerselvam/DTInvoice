package com.dt.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

public class QRCodeGenerator {

    public static byte[] generateQRCode(String text) {

        try {

            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    200,
                    200
            );

            BufferedImage bufferedImage =
                    new BufferedImage(
                            200,
                            200,
                            BufferedImage.TYPE_INT_RGB
                    );

            for (int x = 0; x < 200; x++) {

                for (int y = 0; y < 200; y++) {

                    bufferedImage.setRGB(
                            x,
                            y,
                            bitMatrix.get(x, y)
                                    ? Color.BLACK.getRGB()
                                    : Color.WHITE.getRGB()
                    );
                }
            }

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            ImageIO.write(
                    bufferedImage,
                    "png",
                    outputStream
            );

            return outputStream.toByteArray();

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}