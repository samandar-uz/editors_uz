package org.example.editors_uz.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.editors_uz.entity.abs.BaseEntity;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "orders")
public class Orders extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "access_uuid", unique = true, nullable = false, updatable = false, length = 36)
    private String accessUuid;

    @PrePersist
    public void generateUuid() {
        if (this.accessUuid == null) {
            this.accessUuid = UUID.randomUUID().toString();
        }
    }
}
