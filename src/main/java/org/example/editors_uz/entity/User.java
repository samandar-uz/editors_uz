package org.example.editors_uz.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.editors_uz.entity.abs.BaseEntity;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role = "USER";
    private boolean enabled = true;
    private String key;

}