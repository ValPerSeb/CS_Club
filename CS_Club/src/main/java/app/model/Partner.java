
package app.model;

import java.sql.Date;

public class Partner {
    private long id;
    private long userId;
    private double availBalance;
    private SubscriptionType subscriptionType;
    private Date subscriptionDate;

    public Partner() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getAvailBalance() {
        return availBalance;
    }

    public void setAvailBalance(double availBalance) {
        this.availBalance = availBalance;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }
}
