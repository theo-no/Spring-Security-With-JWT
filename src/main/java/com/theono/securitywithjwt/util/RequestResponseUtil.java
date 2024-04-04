package com.theono.securitywithjwt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theono.securitywithjwt.constant.ErrorCase;
import com.theono.securitywithjwt.model.response.ErrorResponse;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RequestResponseUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String jsonFrom(T obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public static <T> T jsonToObject(ServletInputStream inputStream, Class<T> type)
            throws IOException {
        return objectMapper.readValue(inputStream, type);
    }

    public static void setContentTypeToJson(HttpServletResponse response) {
        response.setContentType("application/json");
    }

    public static void setResponseToErrorResponse(HttpServletResponse response, ErrorCase errorCase)
            throws IOException {
        setContentTypeToJson(response);
        response.setStatus(errorCase.getStatus().value());
        String body =
                jsonFrom(new ErrorResponse(errorCase.getErrorCode(), errorCase.getErrorMessage()));
        response.getWriter().write(body);
    }
}
