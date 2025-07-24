package com.popcoadmin.content.entity;

import com.popcoadmin.content.dto.response.provider.ProviderResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "providers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"watchProviders"})
public class Provider {
    @Id
    private Integer id; // TMDB provider ID

    @Column(nullable = false, length = 500)
    private String name;

    @Column(name = "logo_path", length = 500)
    private String logoPath;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WatchProvider> watchProviders = new ArrayList<>();

    public static Provider from(ProviderResponse dto) {
        Provider provider = new Provider();
        provider.setId(dto.getProviderId());
        provider.setName(dto.getProviderName());
        provider.setLogoPath(dto.getLogoPath());
        return provider;
    }
}
