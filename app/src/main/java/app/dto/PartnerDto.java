
package app.dto;

import app.model.SubscriptionType;
import java.sql.Timestamp;

public class PartnerDto {
    private long id;
    private UserDto userId;
    private double amount;
    private SubscriptionType type;
    private Timestamp creationDate;
    private double totalInvoicesAmountPaid;

    public PartnerDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserDto getUserId() {
        return userId;
    }

    public void setUserId(UserDto userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public SubscriptionType getType() {
        return type;
    }

    public void setType(SubscriptionType type) {
        this.type = type;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public double getTotalInvoicesAmountPaid() {
        return totalInvoicesAmountPaid;
    }

    public void setTotalInvoicesAmountPaid(double totalInvoicesAmountPaid) {
        this.totalInvoicesAmountPaid = totalInvoicesAmountPaid;
    }
    
    @Override
    public String toString() {
        return "----------------------------------------------- \n"
                + "* ID Socio: " + id + "\n"
                + "* ID Usuario: " + userId.getId() + "\n"
                + "* Fecha de afiliación: " + creationDate.toString() + "\n"
                + "* Fondos Disponibles: " + amount + "\n"
                + "* Tipo de suscripción: " + type.toString() + "\n"
                + "----------------------------------------------- \n";
    }
}
