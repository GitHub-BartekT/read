package pl.iseebugs.doread.domain.module;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@Builder
@Table(name = "modules")
@NoArgsConstructor
@AllArgsConstructor
class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "module_name")
    private String moduleName;
    @Column(name = "sessions_Per_Day")
    private int sessionsPerDay;
    @Column(name = "presentations_Per_Session")
    private int presentationsPerSession;
    @Column(name = "new_Sentences_Per_Day")
    private int newSentencesPerDay;
    @Column(name = "actual_Day")
    private int actualDay;
    @Column(name = "next_Session")
    private int nextSession;
    @Column (name = "is_private")
    private boolean isPrivate;
}
