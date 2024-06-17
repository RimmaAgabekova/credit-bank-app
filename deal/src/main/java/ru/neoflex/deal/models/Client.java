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
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "email")
    private String email;

    @Column(name = "gender")
    private String gender;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "dependent_amount")
    private Integer dependentAmount;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "passport_id", referencedColumnName = "passport")
    @Nullable
    private Passport passportId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "employment_id", referencedColumnName = "employment")
    @Nullable
    private Employment employmentId;

    @Column(name = "account_number")
    private String accountNumber;
}
