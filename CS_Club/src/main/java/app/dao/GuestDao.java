
package app.dao;

import app.config.DBConnection;
import app.dto.GuestDto;
import app.model.Guest;
import java.sql.PreparedStatement;

public class GuestDao {
    public void createGuest(GuestDto guestDto) throws Exception {
        Guest guest = Helper.parse(guestDto);
        String query = "INSERT INTO GUEST(STATUS,USERID,PARTNERID) VALUES (?,?,?) ";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setString(1, guest.getStatus().toString());
        preparedStatement.setLong(2, guest.getUserId().getId());
        preparedStatement.setLong(3, guest.getPartnerId().getId());
        preparedStatement.execute();
        preparedStatement.close();
    }
}
