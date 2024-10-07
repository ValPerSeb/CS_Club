
package app.dao;

import app.dao.repositories.PartnerRepository;
import app.dto.InvoiceDto;
import app.dto.PartnerDto;
import app.dto.UserDto;
import app.model.InvoiceStatus;
import app.model.Partner;
import app.model.SubscriptionType;
import app.model.User;
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

public class PartnerDao {
    
    InvoiceDao invoiceDao = new InvoiceDao();
    
    @Autowired
    public PartnerRepository partnerRepository;
    
    public void createPartner(PartnerDto partnerDto) throws Exception {
        Partner partner = Helper.parse(partnerDto);
        partnerRepository.save(partner);
    }
    
    public void updatePartner(PartnerDto partnerDto) throws Exception{
        Partner partner = partnerRepository.getReferenceById(partnerDto.getId());
        if (partner == null) {
            throw new Exception("Socio no encontrado.");
        }
        partner.setAmount(partnerDto.getAmount());
        partner.setType(partnerDto.getType());
        partnerRepository.save(partner);
    }
    
    public PartnerDto findByUserId(UserDto userDto) throws Exception {
        User user = Helper.parse(userDto);
        Partner partner = partnerRepository.findByUserId(user);
        if(partner == null){
            return null;
        }
        return Helper.parse(partner);
    }
    
    public List<PartnerDto> findByType(SubscriptionType type) throws Exception{
        List<PartnerDto> partnersDto = new ArrayList<>();
        List<Partner> partners = partnerRepository.findByType(type);
        
        for(Partner partner : partners) {
            PartnerDto partnerDto = Helper.parse(partner);
            double totalInvoicesAmountPaid = this.getTotalInvoicesAmountPaid(partnerDto);
            partnerDto.setTotalInvoicesAmountPaid(totalInvoicesAmountPaid);
            partnersDto.add(partnerDto);
        }
       
        return partnersDto;
    }
    
    private double getTotalInvoicesAmountPaid(PartnerDto partnerDto) throws Exception{
        List<InvoiceDto> partnerInvoices = invoiceDao.findByPartnerId(partnerDto);
        double total = 0;
        for(InvoiceDto invoice : partnerInvoices) {
            if(invoice.getStatus() == InvoiceStatus.PAID){
                total += invoice.getAmount();
            }
        }
        return total;
    }
}
