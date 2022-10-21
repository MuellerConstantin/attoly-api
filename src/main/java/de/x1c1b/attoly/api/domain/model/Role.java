package de.x1c1b.attoly.api.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
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
    private String name;

    @ManyToMany(mappedBy = "roles")
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<User> users = new HashSet<>();
}
