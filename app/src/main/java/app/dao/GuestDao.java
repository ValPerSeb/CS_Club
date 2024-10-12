
package app.dao;

import app.dao.repositories.GuestRepository;
import app.dto.GuestDto;
import app.dto.PartnerDto;
import app.dto.UserDto;
import app.model.Guest;
import app.model.Partner;
import app.model.User;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@NoArgsConstructor

public class GuestDao {
    
    @Autowired
    public GuestRepository guestRepository;
    
    public void createGuest(GuestDto guestDto) throws Exception {
        Guest guest = Helper.parse(guestDto);
        guestRepository.save(guest);
    }
    
    public List<GuestDto> findByPartnerId(PartnerDto partnerDto) throws Exception {
        Partner partner = Helper.parse(partnerDto);
        List<GuestDto> guestsDto = new ArrayList<>();
        List<Guest> guests = guestRepository.findByPartnerId(partner);
        for(Guest guest : guests){
            GuestDto guestDto = Helper.parse(guest);
            guestsDto.add(guestDto);
        }
        return guestsDto;
    }
    
    @Transactional
    public void updateGuest(GuestDto guestDto) throws Exception {
        Optional<Guest> optionalGuest = guestRepository.findById(guestDto.getId());

        if (optionalGuest.isEmpty()) {
            throw new Exception("Invitado no encontrado.");
        }

        Guest guest = optionalGuest.get();

        guest.setStatus(guestDto.getStatus());
        guestRepository.save(guest);
    }
    
    public GuestDto findByUserId(UserDto userDto) throws Exception {
        User user = Helper.parse(userDto);
        Guest guest = guestRepository.findByUserId(user);
        if(guest == null){
            return null;
        }
        return Helper.parse(guest);
    }
}
