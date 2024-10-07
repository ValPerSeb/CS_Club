
package app.dao;

import app.dao.repositories.InvoiceDetailRepository;
import app.dao.repositories.InvoiceRepository;
import app.dto.InvoiceDetailDto;
import app.dto.InvoiceDto;
import app.dto.PartnerDto;
import app.model.Invoice;
import app.model.InvoiceDetail;
import app.model.Partner;
import app.model.Role;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@NoArgsConstructor

public class InvoiceDao {
    
    @Autowired
    public InvoiceRepository invoiceRepository;
    @Autowired
    public InvoiceDetailRepository invoiceDetailRepository;
    
    public List<InvoiceDto> findAll() throws Exception {
        List<InvoiceDto> invoicesDto = new ArrayList<>();
        List<Invoice> invoices = invoiceRepository.findAll();
        for(Invoice invoice : invoices){
            InvoiceDto invoiceDto = Helper.parse(invoice);
            invoicesDto.add(invoiceDto);
        }
        return invoicesDto;
    }
    
    public List<InvoiceDto> findByRole(Role role) throws Exception {
        List<InvoiceDto> invoicesDto = new ArrayList<>();
        List<Invoice> invoices = invoiceRepository.findByUserRole(role);
        for(Invoice invoice : invoices){
            InvoiceDto invoiceDto = Helper.parse(invoice);
            invoicesDto.add(invoiceDto);
        }
        return invoicesDto;
    }
    
    public List<InvoiceDto> findByPartnerId(PartnerDto partnerDto) throws Exception {
        List<InvoiceDto> invoicesDto = new ArrayList<>();
        Partner partner = Helper.parse(partnerDto);
        List<Invoice> invoices = invoiceRepository.findByPartnerId(partner);
        for(Invoice invoice : invoices){
            InvoiceDto invoiceDto = Helper.parse(invoice);
            invoicesDto.add(invoiceDto);
        }
        return invoicesDto;
    }
    
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) throws Exception {
        Invoice invoice = Helper.parse(invoiceDto);
        Invoice newInvoice = invoiceRepository.save(invoice);
        return Helper.parse(newInvoice);
    }
    
    public void createInvoiceDetail(InvoiceDetailDto invoiceDetailDto) throws Exception {
        InvoiceDetail invoiceDetail = Helper.parse(invoiceDetailDto);
        invoiceDetailRepository.save(invoiceDetail);
    }
    
    public void updateInvoice(InvoiceDto invoiceDto) throws Exception{
        
        Invoice invoice = invoiceRepository.getReferenceById(invoiceDto.getId());
        if (invoice == null) {
            throw new Exception("Factura no encontrada.");
        }
        invoice.setAmount(invoiceDto.getAmount());
        invoice.setStatus(invoiceDto.getStatus());
        invoiceRepository.save(invoice);
    }
}
