
package app.dao;

import app.config.DBConnection;
import app.dto.InvoiceDto;
import app.model.Invoice;
import app.model.InvoiceStatus;
import app.model.Partner;
import app.model.Person;
import app.model.Role;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDao {
    public List<InvoiceDto> getAllInvoices() throws Exception {
        List<InvoiceDto> invoicesDto = new ArrayList<>();
        String query = "SELECT ID,CREATIONDATE,AMOUNT,STATUS,PERSONID,PARTNERID FROM INVOICE";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Invoice invoice = new Invoice();
            invoice.setId(resultSet.getLong("ID"));
            invoice.setCreationDate(resultSet.getTimestamp("CREATIONDATE"));
            invoice.setAmount(resultSet.getDouble("AMOUNT"));
            invoice.setInvoiceStatus(InvoiceStatus.valueOf(resultSet.getString("STATUS")));
            Person person = new Person();
            person.setId(resultSet.getLong("PERSONID"));
            invoice.setPersonId(person);
            Partner partner = new Partner();
            partner.setId(resultSet.getLong("PARTNERID"));
            invoice.setPartnerId(partner);
            
            invoicesDto.add(Helper.parse(invoice));
        }
        resultSet.close();
        preparedStatement.close();
        return invoicesDto;
    }
    
    public List<InvoiceDto> getInvoicesByRole(Role role) throws Exception {
        List<InvoiceDto> invoicesDto = new ArrayList<>();
        String query = "SELECT INVOICE.ID,INVOICE.CREATIONDATE,INVOICE.AMOUNT,INVOICE.STATUS,INVOICE.PERSONID,INVOICE.PARTNERID FROM INVOICE "
                + "JOIN USER ON INVOICE.PERSONID = USER.PERSONNID "
                + "WHERE USER.ROLE = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setString(1, role.toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Invoice invoice = new Invoice();
            invoice.setId(resultSet.getLong("ID"));
            invoice.setCreationDate(resultSet.getTimestamp("CREATIONDATE"));
            invoice.setAmount(resultSet.getDouble("AMOUNT"));
            invoice.setInvoiceStatus(InvoiceStatus.valueOf(resultSet.getString("STATUS")));
            Person person = new Person();
            person.setId(resultSet.getLong("PERSONID"));
            invoice.setPersonId(person);
            Partner partner = new Partner();
            partner.setId(resultSet.getLong("PARTNERID"));
            invoice.setPartnerId(partner);
            
            invoicesDto.add(Helper.parse(invoice));
        }
        resultSet.close();
        preparedStatement.close();
        return invoicesDto;
    }
}
