package com.popcoadmin.content.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "actors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"castRoles"})
public class Actor {
    @Id
    private Long id; // TMDB person ID

    @Column(nullable = false, length = 500)
    private String name;

    @Column(name = "profile_path", length = 500)
    private String profilePath;

    @Column(name = "gender")
    private Integer gender; // 0: Not specified, 1: Female, 2: Male, 3: Non-binary

    @OneToMany(mappedBy = "actor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CastMembers> castRoles = new ArrayList<>();
}
