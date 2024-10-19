package pl.iseebugs.doread.domain.sessionstatistics;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "session_statistics")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionStatistics {

    @SequenceGenerator(
            name = "session_statistics_sequence",
            sequenceName = "session_statistics_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "session_statistics_sequence"
    )
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(name = "sentences_count", nullable = false)
    private int sentencesCount;

    @Column(name = "is_finished", nullable = false)
    private boolean isFinished;

    @Column(name = "ordinal_type", nullable = false)
    private String ordinalType = "QUEUE";

    @Column(name = "modules_in_session", nullable = false)
    private int modulesInSession;

    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime timestamp;

    @PrePersist
    public void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
