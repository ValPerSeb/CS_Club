
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
import app.model.Role;
import app.model.SubscriptionType;
import java.sql.SQLException;
import java.util.List;

public class Service {
    private UserDao userDao;
    private PersonDao personDao;
    private InvoiceDao invoiceDao;
    private PartnerDao partnerDao;
    private GuestDao guestDao;
    public static UserDto user;
    public static PartnerDto partner;
    public static PartnerDto guest;
    
    public Service() {
        this.userDao = new UserDao();
        this.personDao = new PersonDao();
        this.invoiceDao = new InvoiceDao();
        this.partnerDao = new PartnerDao();
        this.guestDao = new GuestDao();
    }
    
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
            // TODO
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
        long[] ids = {userDto.getPersonId().getId()};
        if (this.userDao.existsByUserName(userDto)) {
            this.personDao.deletePerson(ids);
            throw new Exception("Ya existe un usuario con ese Nombre de Usuario");
        }
        try {
            this.userDao.createUser(userDto);
        } catch (SQLException e) {
            this.personDao.deletePerson(ids);
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
            long[] idsPerson = {userDto.getPersonId().getId()};
            long[] idsUser = {partnerDto.getUserId().getId()};
            this.personDao.deletePerson(idsPerson);
            this.userDao.deleteUser(idsUser);
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
            long[] idsPerson = {userDto.getPersonId().getId()};
            long[] idsUser = {guestDto.getUserId().getId()};
            this.personDao.deletePerson(idsPerson);
            this.userDao.deleteUser(idsUser);
            throw new Exception("Error creando Invitado: " + e);
        }
    }
    
    public List<InvoiceDto> getAllInvoices() throws Exception {
        try {
            return this.invoiceDao.getAllInvoices();
        } catch (SQLException e) {
            throw new Exception("Error obteniendo datos de facturas: " + e);
        }
    }
    
    public List<InvoiceDto> getInvoicesByRole(Role role) throws Exception {
        try {
            return this.invoiceDao.getInvoicesByRole(role);
        } catch (SQLException e) {
            throw new Exception("Error obteniendo datos de facturas por rol: " + e);
        }
    }
    
    public List<PartnerDto> getPartnersByType(SubscriptionType type) throws Exception{
        try {
            return this.partnerDao.getPartnersByType(type);
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
            return this.partnerDao.getPartnerByUserId(user.getId());
        } catch (SQLException e) {
            throw new Exception("Error obteniendo datos del Socio: " + e);
        }
    }
     
    public void deleteInvoicesByCurrentPartnerId() throws Exception {
        try {
            List<InvoiceDto> partnerInvoices = this.invoiceDao.getAllInvoicesByPartnerId(partner.getId());
            long[] ids = new long[partnerInvoices.size()];
            for (int i = 0; i < partnerInvoices.size(); i++) {
                ids[i] = partnerInvoices.get(i).getId();
            }
            this.invoiceDao.deleteInvoicesDetailsByInvoiceId(ids);
            this.invoiceDao.deleteInvoicesByPartnerId(partner.getId());
        } catch (SQLException e) {
            throw new Exception("Error eliminando facturas del socio: " + e);
        }
    }
    
    public void deleteGuestsByCurrentPartnerId() throws Exception {
        try {
            List<GuestDto> partnerGuests = this.guestDao.getGuestsByPartnerId(partner.getId());
            long[] ids = new long[partnerGuests.size()];
            for (int i = 0; i < partnerGuests.size(); i++) {
                ids[i] = partnerGuests.get(i).getUserId().getId();
            }
            this.userDao.deleteUser(ids);
            this.guestDao.deleteGuestsByPartnerId(partner.getId());
        } catch (SQLException e) {
            throw new Exception("Error eliminando invitados del socio: " + e);
        }
    }

    public void deleteCurrentPartner() throws Exception {
        try {
            long[] idsPartner = {partner.getId()};
            long[] idsUser = {user.getId()};
            this.partnerDao.deletePartner(idsPartner);
            this.userDao.deleteUser(idsUser);
        } catch (SQLException e) {
            throw new Exception("Error eliminando invitados del socio: " + e);
        }
    }
    
    public List<InvoiceDto> getPendingInvoicesByCurrentPartnerId() throws Exception {
        try {
            return this.invoiceDao.getPendingInvoicesByPartnerId(partner.getId());
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
            throw new Exception("Error obteniendo datos de facturas por rol: " + e);
        }
    }
}
