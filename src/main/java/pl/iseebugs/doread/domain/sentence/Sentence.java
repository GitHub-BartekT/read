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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "module_id")
    private Long moduleId;
    @Column(name = "ordinal_number")
    private Long ordinalNumber;
    @Column(name = "sentence")
    private String sentence;
}
