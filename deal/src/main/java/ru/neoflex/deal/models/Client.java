package ru.neoflex.deal.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "clientSeqGenerator", sequenceName = "client_id_seq", allocationSize = 1)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID clientId;

    @Column
    private String lastName;

    @Column
    private String firstName;

    @Column
    private String middleName;

    @Column
    private LocalDate birthDate;

    @Column
    private String email;

    @Column
    private String gender;

    @Column
    private String maritalStatus;

    @Column
    private Integer dependentAmount;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "passport_id", referencedColumnName = "passport")
    @Nullable
    private Passport passportId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "employment_id", referencedColumnName = "employment")
    @Nullable
    private Employment employmentId;

    @Column
    private String accountNumber;
}
