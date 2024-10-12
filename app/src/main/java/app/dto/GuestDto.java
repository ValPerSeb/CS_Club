
package app.dto;

import app.model.GuestStatus;

public class GuestDto {
    private long id;
    private UserDto userId;
    private PartnerDto partnerId;
    private GuestStatus status;

    public GuestDto() {
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

    public PartnerDto getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(PartnerDto partnerId) {
        this.partnerId = partnerId;
    }

    public GuestStatus getStatus() {
        return status;
    }

    public void setStatus(GuestStatus status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "----------------------------------------------- \n"
                + "* ID: " + id + "\n"
                + "* ID Usuario: " + userId.getId() + "\n"
                + "* ID Socio: " + partnerId.getId() + "\n"
                + "* Estado: " + status.toString() + "\n"
                + "----------------------------------------------- \n";
    }
}


