
package app.dao;

import app.config.DBConnection;
import app.dto.GuestDto;
import app.model.Guest;
import app.model.GuestStatus;
import app.model.Partner;
import app.model.Person;
import app.model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
    
    public List<GuestDto> getGuestsByPartnerId(long partnerId) throws Exception {
        List<GuestDto> guestsDto = new ArrayList<>();
        String query = "SELECT ID,STATUS,USERID,PARTNERID FROM GUEST WHERE PARTNERID = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setLong(1, partnerId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Guest guest = new Guest();
            guest.setId(resultSet.getLong("ID"));
            guest.setStatus(GuestStatus.valueOf(resultSet.getString("STATUS")));
            
            User user = new User();
            user.setId(resultSet.getLong("USERID"));
            guest.setUserId(user);
            
            Partner partner = new Partner();
            partner.setId(resultSet.getLong("PARTNERID"));
            guest.setPartnerId(partner);
            
            guestsDto.add(Helper.parse(guest));
        }
        resultSet.close();
        preparedStatement.close();
        return guestsDto;
    }
    
    public void deleteGuestsByPartnerId(long partnerId) throws Exception {
        String query = "DELETE FROM GUEST WHERE PARTNERID = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setLong(1,partnerId);
        preparedStatement.execute();
        preparedStatement.close();
    }
    
    public void updateGuest(GuestDto guestDto) throws Exception{
        Guest guest = Helper.parse(guestDto);
        String query = "UPDATE GUEST SET STATUS = ? WHERE ID = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setString(1, guest.getStatus().toString());
        preparedStatement.setLong(2, guest.getId());
        preparedStatement.execute();
        preparedStatement.close();
    }
    
    public GuestDto getGuestByUserId(long userId) throws Exception {
        String query = "SELECT ID,USERID,PARTNERID,STATUS FROM GUEST WHERE USERID = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setLong(1, userId);
        ResultSet resulSet = preparedStatement.executeQuery();
        if (resulSet.next()) {
            Guest guest = new Guest();
            guest.setId(resulSet.getLong("ID"));
            guest.setStatus(GuestStatus.valueOf(resulSet.getString("STATUS")));
            
            User user = new User();
            user.setId(resulSet.getLong("USERID"));
            guest.setUserId(user);
            
            Partner partner = new Partner();
            partner.setId(resulSet.getLong("PARTNERID"));
            guest.setPartnerId(partner);
            
            resulSet.close();
            preparedStatement.close();
            return Helper.parse(guest);
        }
        resulSet.close();
        preparedStatement.close();
        return null;
    }
}
