package org.example.editors_uz.entity;

import jakarta.persistence.*;
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
public class Product extends BaseEntity {
    private String name;
    private String description;
    private Integer price;
    @ManyToOne
    private Attachment photo;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private OrderType orderType;

}
