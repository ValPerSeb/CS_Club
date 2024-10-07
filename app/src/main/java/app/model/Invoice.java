
package app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table (name="invoice")

public class Invoice {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;
    @JoinColumn(name="personid")
    @ManyToOne
    private Person personId;
    @JoinColumn(name="partnerid")
    @ManyToOne
    private Partner partnerId;
    @Column(name="creationdate")
    private Timestamp creationDate;
    @Column(name="amount")
    private double amount;
    @Column(name="status")
    private InvoiceStatus status;
}
