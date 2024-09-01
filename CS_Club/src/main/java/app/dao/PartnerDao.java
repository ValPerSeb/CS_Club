
package app.dao;

import app.config.DBConnection;
import java.sql.PreparedStatement;
import app.dto.PartnerDto;
import app.model.Invoice;
import app.model.InvoiceStatus;
import app.model.Partner;
import app.model.SubscriptionType;
import app.model.User;
import java.sql.ResultSet;
import java.util.ArrayList;
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
        List<Invoice> invoicesInfo = new ArrayList<>();
        String query = "SELECT P.ID,P.CREATIONDATE,P.AMOUNT,P.TYPE,P.USERID,I.ID AS INVOICE_ID,I.SATUS,I.AMOUNT AS INVOICE_AMOUNT "
                + "FROM PARTNER P "
                + "JOIN INVOICE I ON I.PARTNERID = P.ID "
                + "WHERE P.TYPE = ?";
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
            
            Invoice invoice = new Invoice();
            invoice.setId(resultSet.getLong("INVOICE_ID"));
            invoice.setStatus(InvoiceStatus.valueOf(resultSet.getString("STATUS")));
            invoice.setAmount(resultSet.getDouble("INVOICE_AMOUNT"));
            invoicesInfo.add(invoice);
            
            PartnerDto partnerDto = Helper.parse(partner);
            partnerDto.setInvoicesInfo(invoicesInfo);
            
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
}
