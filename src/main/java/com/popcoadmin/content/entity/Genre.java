package com.popcoadmin.content.entity;

import com.popcoadmin.content.dto.response.genre.GenreResponse;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "genres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    public static Genre from(GenreResponse dto) {
        Genre genre = new Genre();
        genre.setId(dto.getId());
        genre.setName(dto.getName());
        return genre;
    }
}
