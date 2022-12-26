package de.x1c1b.attoly.api.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "complaints")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Complaint extends BaseEntity {

    @Column(name = "reason", nullable = false)
    @Enumerated(EnumType.STRING)
    private Reason reason;

    @Column(name = "comment", length = 2000)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "shortcut")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Shortcut shortcut;

    public enum Reason {
        SPAM,
        PHISHING,
        MALWARE,
        DEFACEMENT,
    }
}
