package com.yexdynamics.portal_indie.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(length = 20)
    private String version;

    @Column(length = 50)
    private String genre;

    @Column(name = "developer_name", length = 100)
    private String developerName;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "download_url", length = 500)
    private String downloadUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}