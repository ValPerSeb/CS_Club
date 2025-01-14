
package app.dao;

import app.dao.repositories.InvoiceDetailRepository;
import app.dao.repositories.InvoiceRepository;
import app.dto.InvoiceDetailDto;
import app.dto.InvoiceDto;
import app.dto.PartnerDto;
import app.model.Invoice;
import app.model.InvoiceDetail;
import app.model.InvoiceStatus;
import app.model.Partner;
import app.model.Role;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    
    @Transactional
    public void updateInvoice(InvoiceDto invoiceDto) throws Exception{
        
        Optional<Invoice> optionalInvoice  = invoiceRepository.findById(invoiceDto.getId());
        if (optionalInvoice.isEmpty()) {
            throw new Exception("Factura no encontrada.");
        }
        Invoice invoice = optionalInvoice.get();
        invoice.setAmount(invoiceDto.getAmount());
        invoice.setStatus(invoiceDto.getStatus());
        invoiceRepository.save(invoice);
    }
    
    
    public double getTotalInvoicesAmountPaid(PartnerDto partnerDto) throws Exception{
        List<InvoiceDto> partnerInvoices = this.findByPartnerId(partnerDto);
        double total = 0;
        for(InvoiceDto invoice : partnerInvoices) {
            if(invoice.getStatus() == InvoiceStatus.PAID){
                total += invoice.getAmount();
            }
        }
        return total;
    }
}
