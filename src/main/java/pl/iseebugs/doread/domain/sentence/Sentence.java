package pl.iseebugs.doread.domain.sentence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "sentences")
@NoArgsConstructor
@AllArgsConstructor
class Sentence {

    @SequenceGenerator(
            name = "sentences_sequence",
            sequenceName = "sentences_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sentences_sequence"
    )
    private Long id;
    @Column(name = "module_id")
    private Long moduleId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "ordinal_number")
    private Long ordinalNumber;
    @Column(name = "sentence")
    private String sentence;
}
