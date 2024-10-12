
package app.service;

import app.dao.GuestDao;
import app.dao.InvoiceDao;
import app.dao.PartnerDao;
import app.dao.PersonDao;
import app.dao.UserDao;
import app.dto.GuestDto;
import app.dto.InvoiceDetailDto;
import app.dto.InvoiceDto;
import app.dto.PartnerDto;
import app.dto.PersonDto;
import app.dto.UserDto;
import app.model.GuestStatus;
import app.model.InvoiceStatus;
import app.model.Role;
import app.model.SubscriptionType;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@NoArgsConstructor

public class ClubService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private PersonDao personDao;
    @Autowired
    private InvoiceDao invoiceDao;
    @Autowired
    private PartnerDao partnerDao;
    @Autowired
    private GuestDao guestDao;
    @Autowired
    public static UserDto user;
    public static PartnerDto partner;
    public static GuestDto guest;
    
    
    
    public void login(UserDto userDto) throws Exception {
        UserDto validateDto = userDao.findByUserName(userDto);
        if (validateDto == null) {
            throw new Exception("No existe usuario ingresado.");
        }
        if (!userDto.getPassword().equals(validateDto.getPassword())) {
            throw new Exception("Usuario o contraseña incorrecto");
        }
        userDto.setRole(validateDto.getRole());
        user = validateDto;
        if (user.getRole() == Role.PARTNER) {
            partner = this.getCurrentPartner();
        }else if(user.getRole() == Role.GUEST){
            guest = this.getCurrentGuest();
        }
    }

    public void logout() {
        user = null;
        partner = null;
        guest = null;
        System.out.println("Se ha cerrado sesion \n");
    }
    
    public void createPerson(PersonDto personDto) throws Exception {
        if (this.personDao.existsByDocument(personDto)) {
            throw new Exception("Ya existe una persona con ese documento");
        }
        try {
            this.personDao.createPerson(personDto);
        } catch (SQLException e) {
            throw new Exception("Error creando Persona: " + e);
        }
    }
    
    public void createUser(UserDto userDto) throws Exception {
        this.createPerson(userDto.getPersonId());
        PersonDto personDto = personDao.findByDocument(userDto.getPersonId());
        userDto.setPersonId(personDto);
        if (this.userDao.existsByUserName(userDto)) {
            this.personDao.deletePerson(userDto.getPersonId().getId());
            throw new Exception("Ya existe un usuario con ese Nombre de Usuario");
        }
        try {
            this.userDao.createUser(userDto);
        } catch (SQLException e) {
            this.personDao.deletePerson(userDto.getPersonId().getId());
            throw new Exception("Error creando Usuario: " + e.getMessage());
        }
    }
    
    public void createPartner(PartnerDto partnerDto) throws Exception {
        this.createUser(partnerDto.getUserId());
        UserDto userDto = userDao.findByUserName(partnerDto.getUserId());
        partnerDto.setUserId(userDto);
        try {
            this.partnerDao.createPartner(partnerDto);
            System.out.println("Se ha creado el socio correctamente");
        } catch (SQLException e) {
            this.personDao.deletePerson(userDto.getPersonId().getId());
            throw new Exception("Error creando Socio: " + e.getMessage());
        }
        
    }
    
    public void createGuest(GuestDto guestDto) throws Exception {
        guestDto.setPartnerId(partner);
        this.createUser(guestDto.getUserId());
        UserDto userDto = userDao.findByUserName(guestDto.getUserId());
        guestDto.setUserId(userDto);
        try {
            this.guestDao.createGuest(guestDto);
            System.out.println("Se ha creado el invitado correctamente");
        } catch (SQLException e) {
            this.personDao.deletePerson(userDto.getPersonId().getId());
            throw new Exception("Error creando Invitado: " + e);
        }
    }
    
    public void createPartnerFromGuest(PartnerDto partnerDto) throws Exception {
        partnerDto.setUserId(user);
        user.setRole(Role.PARTNER);
        guest.setStatus(GuestStatus.INACTIVE);
        try {
            this.partnerDao.createPartner(partnerDto);
            this.updateUser(user);
            this.updateGuest(guest);
        } catch (SQLException e) {
            throw new Exception("Error creando Socio: " + e.getMessage());
        }
    }
    
    public List<InvoiceDto> getAllInvoices() throws Exception {
        try {
            return this.invoiceDao.findAll();
        } catch (SQLException e) {
            throw new Exception("Error obteniendo datos de facturas: " + e);
        }
    }
    
    public List<InvoiceDto> getInvoicesByRole(Role role) throws Exception {
        try {
            return this.invoiceDao.findByRole(role);
        } catch (SQLException e) {
            throw new Exception("Error obteniendo datos de facturas por rol: " + e);
        }
    }
    
    public List<PartnerDto> getPartnersByType(SubscriptionType type) throws Exception{
        try {
            return this.partnerDao.findByType(type);
        } catch (SQLException e) {
            throw new Exception("Error obteniendo datos de socio por tipo de suscripción: " + e);
        }
    }
    
    public void updatePartner(PartnerDto partnerDto) throws Exception{
        try {
            this.partnerDao.updatePartner(partnerDto);
        } catch (SQLException e) {
            throw new Exception("Error actualizando datos del Socio: " + e);
        }
    }
    
    public PartnerDto getCurrentPartner() throws Exception{
        try {
            return this.partnerDao.findByUserId(user);
        } catch (SQLException e) {
            throw new Exception("Error obteniendo datos del Socio: " + e);
        }
    }
    
    public GuestDto getCurrentGuest() throws Exception{
        try {
            return this.guestDao.findByUserId(user);
        } catch (SQLException e) {
            throw new Exception("Error obteniendo datos del Invitado: " + e);
        }
    }
    public void deleteCurrentUser() throws Exception {
        try {
            this.personDao.deletePerson(user.getPersonId().getId());
        } catch (SQLException e) {
            throw new Exception("Error eliminando datos del socio: " + e);
        }
    }
    
    public List<InvoiceDto> getPendingInvoicesByCurrentPartnerId() throws Exception {
        try {
            List<InvoiceDto> invoices = this.invoiceDao.findByPartnerId(partner);
            List<InvoiceDto> pendingInvoices = invoices.stream()
                                                .filter(invoice -> invoice.getStatus() == InvoiceStatus.PENDING)
                                                .collect(Collectors.toList());
            return pendingInvoices;
        } catch (SQLException e) {
            throw new Exception("Error obteniendo datos de facturas pendientes: " + e);
        }
    }
    
    public void createInvoice(InvoiceDto invoiceDto, InvoiceDetailDto details[]) throws Exception {
        try {
            PersonDto personDto = new PersonDto();
            personDto.setId(user.getPersonId().getId());
            invoiceDto.setPersonId(personDto);
            
            InvoiceDto newInvoice = this.invoiceDao.createInvoice(invoiceDto);
            for (int i = 0; i < details.length; i++) {
                details[i].setInvoiceId(newInvoice);
                this.invoiceDao.createInvoiceDetail(details[i]);
            }
            
            System.out.println("Factura creada con éxito. \n");
        } catch (SQLException e) {
            throw new Exception("Error creando facturas: " + e);
        }
    }
    
    public double payPendingInvoices(double currentAmount) throws Exception {
        List<InvoiceDto> pendingInvoices = this.getPendingInvoicesByCurrentPartnerId();
        double newAmount = currentAmount;
        for(InvoiceDto invoice : pendingInvoices){
            double newAmountToSet = newAmount - invoice.getAmount();
            if(newAmountToSet >= 0){
                invoice.setStatus(InvoiceStatus.PAID);
                this.invoiceDao.updateInvoice(invoice);
                newAmount = newAmountToSet;
                System.out.println("Factura " + invoice.getId() + " pagada: -" + invoice.getAmount() + "\n");
            }
        }
        System.out.println("Nuevo saldo: " + newAmount + "\n");
        return newAmount;
    }
    
    public List<GuestDto> getGuestsByCurrentPartner() throws Exception {
        try {
            return this.guestDao.findByPartnerId(partner);
        } catch (SQLException e) {
            throw new Exception("Error obteniendo datos de invitados del socio: " + e);
        }
    }
    
    public void updateGuest(GuestDto guestDto) throws Exception{
        try {
            this.guestDao.updateGuest(guestDto);
        } catch (SQLException e) {
            throw new Exception("Error actualizando datos del Invitado: " + e);
        }
    }
    
    public void updateUser(UserDto userDto) throws Exception{
        try {
            this.userDao.updateUser(userDto);
        } catch (SQLException e) {
            throw new Exception("Error actualizando datos del usuario: " + e);
        }
    }
    
    public List<InvoiceDto> getAllInvoicesByPartnerId() throws Exception {
        try {
            return this.invoiceDao.findByPartnerId(partner);
        } catch (SQLException e) {
            throw new Exception("Error obteniendo datos de facturas: " + e);
        }
    }
}
