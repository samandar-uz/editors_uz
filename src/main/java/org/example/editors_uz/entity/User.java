package org.example.editors_uz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.editors_uz.entity.abs.BaseEntity;

@Entity
@Table(name = "users", indexes = {@Index(name = "idx_username", columnList = "username"), @Index(name = "idx_key", columnList = "key")})
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class User extends BaseEntity {

    @NotBlank(message = "Username bo'sh bo'lmasligi kerak")
    @Size(min = 3, max = 50, message = "Username 3-50 belgi orasida bo'lishi kerak")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "Parol bo'sh bo'lmasligi kerak")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Role bo'sh bo'lmasligi kerak")
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String role = "USER";

    @Min(value = 0, message = "Balans manfiy bo'lishi mumkin emas")
    @Column(nullable = false)
    @Builder.Default
    private Integer salary = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(unique = true)
    private String key;

}