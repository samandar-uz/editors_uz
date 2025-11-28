package org.example.editors_uz.util;

import org.springframework.stereotype.Component;

import java.io.PrintWriter;
@Component
public class WriteField {
    public void writeField(PrintWriter writer, String boundary, String name, String value) {
        writer.append("--").append(boundary).append("\r\n")
                .append("Content-Disposition: form-data; name=\"").append(name).append("\"\r\n\r\n")
                .append(value).append("\r\n");
        writer.flush();
    }
}
