package org.example.editors_uz.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.example.editors_uz.entity.Attachment;
import org.example.editors_uz.entity.abs.BaseEntity;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
public class AttachmentContent  extends BaseEntity {
    @ManyToOne
    private Attachment attachment;
    private byte[] content;
}
