package org.example.editors_uz.util;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
@Component
public class ReadErrorResponse {
    public String readErrorResponse(HttpURLConnection conn) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
            return reader.lines().reduce("", String::concat);
        } catch (Exception e) {
            return "Unknown error";
        }

    }
}
