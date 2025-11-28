package org.example.editors_uz.util;

import org.springframework.stereotype.Component;

@Component
public class Sanitize {
    public String sanitizeFilename(String filename) {
        return filename == null ? "file" : filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
