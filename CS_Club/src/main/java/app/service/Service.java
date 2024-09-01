
package app.service;

import app.dao.GuestDao;
import app.dao.InvoiceDao;
import app.dao.PartnerDao;
import app.dao.PersonDao;
import app.dao.UserDao;
import app.dto.GuestDto;
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
    }

    public void logout() {
        user = null;
        System.out.println("Se ha cerrado sesion");
    }
    
    public void createPerson(PersonDto personDto) throws Exception {
        if (this.personDao.existsByDocument(personDto)) {
            throw new Exception("Ya existe una persona con ese documento");
        }
        try {
            this.personDao.createPerson(personDto);
        } catch (SQLException e) {
            throw new Exception("Error creando Persona: " + e.getMessage());
        }
    }
    
    public void createUser(UserDto userDto) throws Exception {
        this.createPerson(userDto.getPersonId());
        PersonDto personDto = personDao.findByDocument(userDto.getPersonId());
        userDto.setPersonId(personDto);
        if (this.userDao.existsByUserName(userDto)) {
            this.personDao.deletePerson(userDto.getPersonId());
            throw new Exception("Ya existe un usuario con ese Nombre de Usuario");
        }
        try {
            this.userDao.createUser(userDto);
        } catch (SQLException e) {
            this.personDao.deletePerson(userDto.getPersonId());
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
            this.personDao.deletePerson(userDto.getPersonId());
            this.userDao.deleteUser(partnerDto.getUserId());
            throw new Exception("Error creando Socio: " + e.getMessage());
        }
        
    }
    
    public void createGuest(GuestDto guestDto) throws Exception {
        this.createUser(guestDto.getUserId());
        UserDto userDto = userDao.findByUserName(guestDto.getUserId());
        guestDto.setUserId(userDto);
        try {
            this.guestDao.createGuest(guestDto);
            System.out.println("Se ha creado el invitado correctamente");
        } catch (SQLException e) {
            this.personDao.deletePerson(userDto.getPersonId());
            this.userDao.deleteUser(guestDto.getUserId());
            throw new Exception("Error creando Invitado: " + e.getMessage());
        }
    }
    
    public List<InvoiceDto> getAllInvoices() throws Exception {
        try {
            return this.invoiceDao.getAllInvoices();
        } catch (SQLException e) {
            throw new Exception("Error obteniendo datos de facturas");
        }
    }
    
    public List<InvoiceDto> getInvoicesByRole(Role role) throws Exception {
        try {
            return this.invoiceDao.getInvoicesByRole(role);
        } catch (SQLException e) {
            throw new Exception("Error obteniendo datos de facturas por rol");
        }
    }
    
    public List<PartnerDto> getPartnersByType(SubscriptionType type) throws Exception{
        try {
            return this.partnerDao.getPartnersByType(type);
        } catch (SQLException e) {
            throw new Exception("Error obteniendo datos de socio por tipo de suscripción");
        }
    }
    
    public void updatePartner(PartnerDto partnerDto) throws Exception{
        try {
            this.partnerDao.updatePartner(partnerDto);
        } catch (SQLException e) {
            throw new Exception("Error actualizando datos del Socio");
        }
    }
}
