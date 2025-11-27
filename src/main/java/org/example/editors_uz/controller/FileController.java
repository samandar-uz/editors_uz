package org.example.editors_uz.controller;


import jakarta.servlet.http.HttpServletResponse;
import org.example.editors_uz.entity.AttachmentContent;
import org.example.editors_uz.repository.AttachmentContentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/file")
public class FileController {

    private final AttachmentContentRepository attachmentContentRepository;

    public FileController(AttachmentContentRepository attachmentContentRepository) {
        this.attachmentContentRepository = attachmentContentRepository;
    }

    @GetMapping("/{attachmentId}")
    public void getFile(@PathVariable Integer attachmentId, HttpServletResponse response) throws IOException {
        AttachmentContent byAttachmentId = attachmentContentRepository.findByAttachmentId(attachmentId);
        response.getOutputStream().write(byAttachmentId.getContent());
    }

}