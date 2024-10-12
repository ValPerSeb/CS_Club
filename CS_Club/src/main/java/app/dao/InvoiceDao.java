
package app.dao;

import app.config.DBConnection;
import app.dto.InvoiceDetailDto;
import app.dto.InvoiceDto;
import app.dto.PartnerDto;
import app.model.Invoice;
import app.model.InvoiceDetail;
import app.model.InvoiceStatus;
import app.model.Partner;
import app.model.Person;
import app.model.Role;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
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
            invoice.setStatus(InvoiceStatus.valueOf(resultSet.getString("STATUS")));
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
        String query = "SELECT I.ID,I.CREATIONDATE,I.AMOUNT,I.STATUS,I.PERSONID,I.PARTNERID FROM INVOICE I "
                + "JOIN USER U ON I.PERSONID = U.PERSONNID "
                + "WHERE U.ROLE = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setString(1, role.toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Invoice invoice = new Invoice();
            invoice.setId(resultSet.getLong("ID"));
            invoice.setCreationDate(resultSet.getTimestamp("CREATIONDATE"));
            invoice.setAmount(resultSet.getDouble("AMOUNT"));
            invoice.setStatus(InvoiceStatus.valueOf(resultSet.getString("STATUS")));
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
    
    public List<InvoiceDto> getPendingInvoicesByPartnerId(long partnerId) throws Exception {
        List<InvoiceDto> invoicesDto = new ArrayList<>();
        String query = "SELECT ID,CREATIONDATE,AMOUNT,STATUS,PERSONID,PARTNERID FROM INVOICE "
                + "WHERE PARTNERID = ? AND STATUS = 'PENDING' ORDER BY CREATIONDATE DESC";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setLong(1, partnerId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Invoice invoice = new Invoice();
            invoice.setId(resultSet.getLong("ID"));
            invoice.setCreationDate(resultSet.getTimestamp("CREATIONDATE"));
            invoice.setAmount(resultSet.getDouble("AMOUNT"));
            invoice.setStatus(InvoiceStatus.valueOf(resultSet.getString("STATUS")));
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
    
    public void deleteInvoicesByPartnerId(long partnerId) throws Exception {
        String query = "DELETE FROM INVOICE WHERE PARTNERID = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setLong(1,partnerId);
        preparedStatement.execute();
        preparedStatement.close();
    }
       
    public void deleteInvoicesDetailsByInvoiceId(long[] ids) throws Exception {
        if (ids == null || ids.length == 0) {
            throw new Exception("Error en la lista de IDS.");
        }
        String placeholders = String.join(",", Collections.nCopies(ids.length, "?"));
        String query = "DELETE FROM INVOICEDETAIL WHERE INVOICEID IN (" + placeholders + ")";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        for (int i = 0; i < ids.length; i++) {
            preparedStatement.setLong(i + 1, ids[i]);
        }
        preparedStatement.execute();
        preparedStatement.close();	
    }
    
    public List<InvoiceDto> getAllInvoicesByPartnerId(long partnerId) throws Exception {
        List<InvoiceDto> invoicesDto = new ArrayList<>();
        String query = "SELECT ID,CREATIONDATE,AMOUNT,STATUS,PERSONID,PARTNERID FROM INVOICE "
                + "WHERE PARTNERID = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setLong(1, partnerId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Invoice invoice = new Invoice();
            invoice.setId(resultSet.getLong("ID"));
            invoice.setCreationDate(resultSet.getTimestamp("CREATIONDATE"));
            invoice.setAmount(resultSet.getDouble("AMOUNT"));
            invoice.setStatus(InvoiceStatus.valueOf(resultSet.getString("STATUS")));
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
    
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) throws Exception {
        Invoice invoice = Helper.parse(invoiceDto);
        String query = "INSERT INTO INVOICE(AMOUNT,CREATIONDATE,PARTNERID,PERSONID,STATUS) VALUES (?,?,?,?,?) ";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setDouble(1, invoice.getAmount());
        preparedStatement.setTimestamp(2,invoice.getCreationDate());
        preparedStatement.setLong(3, invoice.getPartnerId().getId());
        preparedStatement.setLong(4, invoice.getPersonId().getId());
        preparedStatement.setString(5, invoice.getStatus().toString());
        preparedStatement.execute();
        preparedStatement.close();
        
        List<InvoiceDto> invoices = this.getPendingInvoicesByPartnerId(invoice.getPartnerId().getId());
        return invoices.get(0);
    }
    
    public void createInvoiceDetail(InvoiceDetailDto invoiceDetailDto) throws Exception {
        InvoiceDetail invoiceDetail = Helper.parse(invoiceDetailDto);
        String query = "INSERT INTO INVOICEDETAIL(AMOUNT,DESCRIPTION,INVOICEID,ITEM) VALUES (?,?,?,?) ";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setDouble(1, invoiceDetail.getAmount());
        preparedStatement.setString(2,invoiceDetail.getDescription());
        preparedStatement.setLong(3, invoiceDetail.getInvoiceId().getId());
        preparedStatement.setInt(4, invoiceDetail.getItem());
        preparedStatement.execute();
        preparedStatement.close();
    }
    
    public void updateInvoice(InvoiceDto invoiceDto) throws Exception{
        Invoice invoice = Helper.parse(invoiceDto);
        String query = "UPDATE INVOICE SET STATUS = ? WHERE ID = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setString(1, invoice.getStatus().toString());
        preparedStatement.setLong(2, invoice.getId());
        preparedStatement.execute();
        preparedStatement.close();
    }
}
