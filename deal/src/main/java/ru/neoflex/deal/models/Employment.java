package ru.neoflex.deal.models;

import jakarta.persistence.*;
import lombok.*;
import ru.neoflex.deal.model.dto.EmploymentDTO;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "employment")
    private UUID employment;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EmploymentDTO.EmploymentStatusEnum status;

    @Column(name = "employer_inn")
    private String employerInn;

    @Column(name = "salary")
    private BigDecimal salary;

    @Column(name = "position")
    @Enumerated(EnumType.STRING)
    private EmploymentDTO.PositionEnum position;

    @Column(name = "work_experience_total")
    private Integer workExperienceTotal;

    @Column(name = "work_experience_current")
    private Integer workExperienceCurrent;


}
