package com.popcoadmin.content.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "director")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "director_id")
    private Long directorId;

    @Column(nullable = false)
    private String name;

    @Column(name = "profile_path")
    private String profilePath;

    @OneToMany(mappedBy = "director", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ContentDirector> contentDirectors = new ArrayList<>();
}
