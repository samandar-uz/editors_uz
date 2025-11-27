package org.example.editors_uz.service;


import org.example.editors_uz.entity.Attachment;
import org.example.editors_uz.entity.AttachmentContent;
import org.example.editors_uz.repository.AttachmentContentRepository;
import org.example.editors_uz.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService {
    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;

    public FileService(AttachmentRepository attachmentRepository, AttachmentContentRepository attachmentContentRepository) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
    }

    public Attachment saveFile(MultipartFile file) {
        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setContentType(file.getContentType());
        Attachment save = attachmentRepository.save(attachment);
        saveAttachmentContent(save, file);
        return save;
    }

    public void saveAttachmentContent(Attachment attachment, MultipartFile file) {
        try {
            AttachmentContent attachmentContent = new AttachmentContent();
            attachmentContent.setAttachment(attachment);
            attachmentContent.setContent(file.getBytes());
            attachmentContentRepository.save(attachmentContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
