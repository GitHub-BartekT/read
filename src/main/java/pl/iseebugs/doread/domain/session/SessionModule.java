package pl.iseebugs.doread.domain.session;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sessions_modules", uniqueConstraints = {@UniqueConstraint(columnNames = {"sessions_id", "module_id"})})
class SessionModule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sessions_modules_sequence")
    @SequenceGenerator(name = "sessions_modules_sequence", sequenceName = "sessions_modules_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sessions_id", nullable = false)
    private Session session;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(name = "ordinal_position", nullable = false, columnDefinition = "BIGINT DEFAULT 1")
    private Long ordinalPosition;
}
