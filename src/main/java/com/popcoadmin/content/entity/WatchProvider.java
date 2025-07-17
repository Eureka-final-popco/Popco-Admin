package com.popcoadmin.content.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "watch_provider")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "content_id", referencedColumnName = "id", nullable = false),
            @JoinColumn(name = "content_type", referencedColumnName = "type", nullable = false)
    })
    private Content content;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WatchProvider that = (WatchProvider) o;
        // provider.getId()와 content.getId()만 사용
        return Objects.equals(provider != null ? provider.getId() : null,
                that.provider != null ? that.provider.getId() : null) &&
                Objects.equals(content != null ? content.getId() : null,
                        that.content != null ? that.content.getId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                provider != null ? provider.getId() : null,
                content != null ? content.getId() : null
        );
    }
}
