
package app.dao;

import app.dao.repositories.PartnerRepository;
import app.dto.InvoiceDto;
import app.dto.PartnerDto;
import app.dto.UserDto;
import app.model.InvoiceStatus;
import app.model.Partner;
import app.model.SubscriptionType;
import app.model.User;
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

public class PartnerDao {
    @Autowired
    public PartnerRepository partnerRepository;
    @Autowired
    private InvoiceDao invoiceDao;
    
    public void createPartner(PartnerDto partnerDto) throws Exception {
        Partner partner = Helper.parse(partnerDto);
        partnerRepository.save(partner);
    }
    
    @Transactional
    public void updatePartner(PartnerDto partnerDto) throws Exception{
        Optional<Partner> optionalPartner = partnerRepository.findById(partnerDto.getId());
        if (optionalPartner.isEmpty()) {
            throw new Exception("Socio no encontrado.");
        }
        Partner partner = optionalPartner.get();
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
            double totalInvoicesAmountPaid = invoiceDao.getTotalInvoicesAmountPaid(partnerDto);
            partnerDto.setTotalInvoicesAmountPaid(totalInvoicesAmountPaid);
            partnersDto.add(partnerDto);
        }
       
        return partnersDto;
    }
}
