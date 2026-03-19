package org.example.spont.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "name", nullable = false,length = 100)
    @NotBlank
    private String name;

    @Column(name = "email",unique = true,length = 100)
    private String email;

    @Column(name = "phone",unique = true,length = 15,nullable = false)
    @NotBlank
    private String phone;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type",nullable = false)
    private UserType userType;

    @Column(name = "gender",nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

//    audit fields

    @CreationTimestamp
    @Column(name = "created_at",nullable = false)
    private Instant createdAt;


}
