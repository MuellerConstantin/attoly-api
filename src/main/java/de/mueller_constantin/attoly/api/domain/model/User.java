package de.mueller_constantin.attoly.api.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class User extends BaseEntity {

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(name = "locked", nullable = false)
    @Builder.Default
    private boolean locked = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_provider")
    private IdentityProvider identityProvider;

    @Column(name = "identity_provider_id")
    private String identityProviderId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.REMOVE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private List<Shortcut> shortcuts = new ArrayList<>();
}
