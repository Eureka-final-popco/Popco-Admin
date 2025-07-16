package com.popcoadmin.content.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "genre")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;
}
