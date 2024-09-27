package pl.iseebugs.doread.domain.session;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sessions_sequence")
    @SequenceGenerator(name = "sessions_sequence", sequenceName = "sessions_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "group_name", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'new group session'")
    private String groupName;

    @Enumerated(EnumType.STRING)
    @Column(name = "ordinal_type", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'QUEUE'")
    private OrdinalType ordinalType;

    @Column(name = "ordinal_schema")
    private String ordinalSchema;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordinalPosition ASC")
    private List<SessionModule> sessionModules;
}
