package ru.neoflex.deal.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.neoflex.deal.model.dto.EmploymentDTO;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "employmentSeqGenerator", sequenceName = "employment_id_seq", allocationSize = 1)
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID employment;

    @Column
    @Enumerated(EnumType.STRING)
    private EmploymentDTO.EmploymentStatusEnum status;

    @Column
    private String employerInn;

    @Column
    private BigDecimal salary;

    @Column
    @Enumerated(EnumType.STRING)
    private EmploymentDTO.PositionEnum position;

    @Column
    private Integer workExperienceTotal;

    @Column
    private Integer workExperienceCurrent;


}
