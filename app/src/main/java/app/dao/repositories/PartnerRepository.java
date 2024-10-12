
package app.dao.repositories;

import app.model.Partner;
import app.model.SubscriptionType;
import app.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long>{

    public Partner findByUserId(User user);

    public List<Partner> findByType(SubscriptionType type);
}
