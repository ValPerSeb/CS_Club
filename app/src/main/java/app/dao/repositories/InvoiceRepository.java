
package app.dao.repositories;

import app.model.Invoice;
import app.model.Partner;
import app.model.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>{

    public List<Invoice> findByPartnerId(Partner partner);
    
    @Query("SELECT i FROM Invoice i " +
       "JOIN i.personId p " +
       "JOIN User u ON u.personId.id = p.id " +
       "WHERE u.role = :role")
    public List<Invoice> findByUserRole(@Param("role") Role role);
    
}
