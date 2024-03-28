package com.theono.securitywithjwt.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;

import java.io.IOException;

public class RequestResponseUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T jsonToObject(ServletInputStream inputStream, Class<T> type)
            throws IOException {
        return objectMapper.readValue(inputStream, type);
    }
}
