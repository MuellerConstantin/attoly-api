package de.mueller_constantin.attoly.api.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shortcuts", uniqueConstraints = {
        @UniqueConstraint(columnNames = "tag")
})
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Shortcut extends BaseEntity {

    @Column(name = "tag", unique = true, nullable = false)
    private String tag;

    @Column(name = "url", length = 2000, nullable = false)
    private String url;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "created_by")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User createdBy;

    @OneToMany(mappedBy = "shortcut", cascade = CascadeType.REMOVE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private List<Complaint> complaints = new ArrayList<>();
}
