package ru.neoflex.deal.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "passportSeqGenerator", sequenceName = "passport_id_seq", allocationSize = 1)
public class Passport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID passport;
    @Column
    private String series;
    @Column
    private String number;
    @Column
    private String issueBranch;
    @Column
    private LocalDate issueDate;
}
