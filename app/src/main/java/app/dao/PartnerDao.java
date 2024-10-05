
package app.dao;

import app.config.DBConnection;
import app.dao.repositories.PartnerRepository;
import java.sql.PreparedStatement;
import app.dto.PartnerDto;
import app.dto.UserDto;
import app.model.Partner;
import app.model.SubscriptionType;
import app.model.User;
import java.sql.ResultSet;
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
    
    @Autowired
    public PartnerRepository partnerRepository;
    
    public void createPartner(PartnerDto partnerDto) throws Exception {
        Partner partner = Helper.parse(partnerDto);
        partnerRepository.save(partner);
    }
    
    /*public List<PartnerDto> getPartnersByType(SubscriptionType type) throws Exception{
        List<PartnerDto> partnersDto = new ArrayList<>();
       String query = "SELECT P.ID,P.CREATIONDATE,P.AMOUNT,P.TYPE,P.USERID, SUM(I.AMOUNT) AS TOTAL_INVOICE_AMOUNT "
                + "FROM PARTNER P "
                + "LEFT JOIN INVOICE I ON I.PARTNERID = P.ID AND I.STATUS = 'PAID' "
                + "WHERE P.TYPE = ? "
                + "GROUP BY P.ID, P.CREATIONDATE, P.AMOUNT, P.TYPE, P.USERID";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setString(1, type.toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Partner partner = new Partner();
            partner.setId(resultSet.getLong("ID"));
            partner.setCreationDate(resultSet.getTimestamp("CREATIONDATE"));
            partner.setAmount(resultSet.getDouble("AMOUNT"));
            partner.setType(SubscriptionType.valueOf(resultSet.getString("TYPE")));
            
            User user = new User();
            user.setId(resultSet.getLong("USERID"));
            partner.setUserId(user);
            
            PartnerDto partnerDto = Helper.parse(partner);
            partnerDto.setTotalInvoicesAmountPaid(resultSet.getDouble("TOTAL_INVOICE_AMOUNT"));
            
            partnersDto.add(partnerDto);
        }
        resultSet.close();
        preparedStatement.close();
        return partnersDto;
       
    }*/
    
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
}
