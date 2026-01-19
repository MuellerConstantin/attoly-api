package de.mueller_constantin.attoly.api.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Role extends BaseEntity {

    @Column(name = "name", unique = true)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @ManyToMany(mappedBy = "roles")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private Set<User> users = new HashSet<>();
}
