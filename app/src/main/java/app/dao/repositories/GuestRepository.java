
package app.dao.repositories;

import app.model.Guest;
import app.model.Partner;
import app.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long>{

    public List<Guest> findByPartnerId(Partner partner);

    public Guest findByUserId(User user);
    
}
