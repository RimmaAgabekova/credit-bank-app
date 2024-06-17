package ru.neoflex.deal.models;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.neoflex.deal.model.dto.LoanOfferDTO;
import ru.neoflex.deal.model.dto.StatementStatus;
import ru.neoflex.deal.model.dto.StatementStatusHistoryDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID statementId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "client_id")
    private Client clientId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "credit_id")
    private Credit creditId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatementStatus status;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applied_offer", columnDefinition = "jsonb")
    private LoanOfferDTO appliedOffer;

    @Column(name = "sign_date")
    private LocalDate signDate;

    @Column(name = "ses_code")
    private Integer sesCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history", columnDefinition = "jsonb")
    private List<StatementStatusHistoryDTO> statusHistory;

}
