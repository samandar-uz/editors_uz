package org.example.editors_uz.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.editors_uz.entity.abs.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
@Table(name = "templates")
public class Templates extends BaseEntity {
    private String title;
    private String description;
    private String price;
    private String photo;
    private String file;
}
