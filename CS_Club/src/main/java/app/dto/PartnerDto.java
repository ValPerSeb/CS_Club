
package app.dto;

import app.model.Invoice;
import app.model.InvoiceStatus;
import app.model.SubscriptionType;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PartnerDto {
    private long id;
    private UserDto userId;
    private double amount;
    private SubscriptionType type;
    private Timestamp creationDate;
    private final double maxAmountVIP = 5000000;
    private final double maxAmountRegular = 1000000;
    private List<Invoice> invoicesInfo = new ArrayList<>();

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

    public List<Invoice> getInvoicesInfo() {
        return invoicesInfo;
    }

    public void setInvoicesInfo(List<Invoice> invoicesInfo) {
        this.invoicesInfo = invoicesInfo;
    }

    public double calcPaidInvoicesTotal() {
        double paidInvoicesTotal = 0;
        for(Invoice invoice : this.getInvoicesInfo()){
            if(invoice.getStatus() == InvoiceStatus.PAID){
                paidInvoicesTotal += invoice.getAmount();
            }
        }
        return paidInvoicesTotal;
    }
    
    public void increaseAmount(double addAmount) throws Exception{
        double newAmount = this.amount + addAmount;
        if(this.type == SubscriptionType.VIP && newAmount > this.maxAmountVIP){
            throw new Exception("Nuevo monto de fondos supera el máximo permitido \n"
                + "- VIP: " + this.maxAmountVIP + "\n"
                + "- REGULARES: " + this.maxAmountRegular + "\n");
        }
        this.amount = newAmount;
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
