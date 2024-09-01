
package app.dao;

import app.config.DBConnection;
import java.sql.PreparedStatement;
import app.dto.PartnerDto;
import app.model.Partner;
import app.model.SubscriptionType;
import app.model.User;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartnerDao {
    public void createPartner(PartnerDto partnerDto) throws Exception {
        Partner partner = Helper.parse(partnerDto);
        String query = "INSERT INTO PARTNER(AMOUNT,CREATIONDATE,TYPE,USERID) VALUES (?,?,?,?) ";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setDouble(1, partner.getAmount());
        preparedStatement.setTimestamp(2,partner.getCreationDate());
        preparedStatement.setString(3, partner.getType().toString());
        preparedStatement.setLong(4, partner.getUserId().getId());
        preparedStatement.execute();
        preparedStatement.close();
    }
    
    public List<PartnerDto> getPartnersByType(SubscriptionType type) throws Exception{
        List<PartnerDto> partnersDto = new ArrayList<>();
        String query = "SELECT P.ID,P.CREATIONDATE,P.AMOUNT,P.TYPE,P.USERID, SUM(I.AMOUNT) AS TOTAL_INVOICE_AMOUNT "
                + "FROM PARTNER P "
                + "LEFT JOIN INVOICE I ON I.PARTNERID = P.ID AND I.STATUS = 'PAID' "
                + "WHERE P.TYPE = ? "
                + "GROUP BY P.ID, P.CREATIONDATE, P.AMOUNT, P.TYPE, P.USERID";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setString(1, type.toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Partner partner = new Partner();
            partner.setId(resultSet.getLong("ID"));
            partner.setCreationDate(resultSet.getTimestamp("CREATIONDATE"));
            partner.setAmount(resultSet.getDouble("AMOUNT"));
            partner.setType(SubscriptionType.valueOf(resultSet.getString("TYPE")));
            
            User user = new User();
            user.setId(resultSet.getLong("USERID"));
            partner.setUserId(user);
            
            PartnerDto partnerDto = Helper.parse(partner);
            partnerDto.setTotalInvoicesAmountPaid(resultSet.getDouble("TOTAL_INVOICE_AMOUNT"));
            
            partnersDto.add(partnerDto);
        }
        resultSet.close();
        preparedStatement.close();
        return partnersDto;
    }
    
    public void updatePartner(PartnerDto partnerDto) throws Exception{
        Partner partner = Helper.parse(partnerDto);
        String query = "UPDATE PARTNER SET AMOUNT = ?, CREATIONDATE = ?, TYPE = ? WHERE ID = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setDouble(1, partner.getAmount());
        preparedStatement.setTimestamp(2,partner.getCreationDate());
        preparedStatement.setString(3, partner.getType().toString());
        preparedStatement.setLong(4, partner.getId());
        preparedStatement.execute();
        preparedStatement.close();
    }
    
    public PartnerDto getPartnerByUserId(long userId) throws Exception {
        String query = "SELECT ID,AMOUNT,CREATIONDATE,TYPE,USERID FROM PARTNER WHERE USERID = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setLong(1, userId);
        ResultSet resulSet = preparedStatement.executeQuery();
        if (resulSet.next()) {
            Partner partner = new Partner();
            partner.setId(resulSet.getLong("ID"));
            partner.setAmount(resulSet.getDouble("AMOUNT"));
            partner.setCreationDate(resulSet.getTimestamp("CREATIONDATE"));;
            partner.setType(SubscriptionType.valueOf(resulSet.getString("type")));
            
            User user = new User();
            user.setId(resulSet.getLong("USERID"));
            resulSet.close();
            preparedStatement.close();
            return Helper.parse(partner);
        }
        resulSet.close();
        preparedStatement.close();
        return null;
    }
    
    public void deletePartner(long[] ids) throws Exception {
        if (ids == null || ids.length == 0) {
            throw new Exception("Error en la lista de IDS.");
        }
        String placeholders = String.join(",", Collections.nCopies(ids.length, "?"));
        String query = "DELETE FROM PARTNER WHERE ID IN (" + placeholders + ")";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        for (int i = 0; i < ids.length; i++) {
            preparedStatement.setLong(i + 1, ids[i]);
        }
        preparedStatement.execute();
        preparedStatement.close();	
    }
}
