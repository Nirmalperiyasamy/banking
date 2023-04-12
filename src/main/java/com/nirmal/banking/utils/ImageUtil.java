package com.nirmal.banking.utils;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.exception.CustomException;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ImageUtil {

    public static byte[] compressImage(byte[] data) {

        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] temporary = new byte[1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(temporary);
            outputStream.write(temporary, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception ignored) {
            throw new CustomException(ErrorMessages.COMPRESS_ERROR);
        }
        return outputStream.toByteArray();
    }


    public static byte[] decompressImage(byte[] data) {

        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] temporary = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(temporary);
                outputStream.write(temporary, 0, count);
            }
            outputStream.close();
        } catch (Exception ignored) {
            throw new CustomException(ErrorMessages.DECOMPRESS_ERROR);
        }
        return outputStream.toByteArray();
    }
}
