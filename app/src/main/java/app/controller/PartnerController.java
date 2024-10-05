
package app.controller;

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
import app.service.Service;
import static app.service.Service.user;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class PartnerController implements ControllerInterface{
    private Service service;
    private static final String MENU = "Ingrese la opción deseada: \n 1. Crear Invitado. "
            + "\n 2. Activar Invitado. "
            + "\n 3. Desactivar Invitado. "
            + "\n 4. Hacer consumo. "
            + "\n 5. Subir fondos. "
            + "\n 6. Solicitar baja. "
            + "\n 7. Solicitar promoción a VIP."
            + "\n 8. Historial de Facturas Socio."
            + "\n 9. Cerrar Sesión. \n";

    public PartnerController(){
        this.service = new Service();
    }
  
    
    @Override
    public void session() throws Exception {
        boolean session = true;
        while (session) {
            session = menu();
        }
    }
    
    private boolean menu() {
        try {
            System.out.println("*** Bienvenido ***");
            System.out.print(MENU);
            String option = Utils.getReader().nextLine();
            return options(option);
        } catch (Exception e) {
                System.out.println(e.getMessage());
                return true;
        }
    }
    
    private boolean options(String option) throws Exception{
        switch (option) {
            case "1": {
                System.out.println("***Nuevo Invitado***\n");
                this.createGuest();
                return true;
            }
            case "2": {
                System.out.println("***Activación de Invitado***");
                this.activateGuest();
                return true;
            }
            case "3": {
                System.out.println("***Desactivación de Invitado***");
                this.deactivateGuest();
                return true;
            }
            case "4": {
                System.out.println("***Nuevo Consumo***");
                this.newService();
                return true;
            }
            case "5": {
                System.out.println("***Subir fondos***");
                this.increaseAmount();
                return true;
            }
            case "6": {
                System.out.println("***Cancelar suscripción***");
                this.cancelSubscription();
                return!(user == null);
            }
            case "7": {
                System.out.println("***Solicitar Promoción a VIP***");
                this.requestVIPUpgrade();
                return true;
            }
            case "8": {
                System.out.println("***Historial de Facturas Socio***");
                this.showInvoices();
                return true;
            }
            case "9": {
                System.out.println("Se ha cerrado sesión.");
                return false;
            }
            default: {
                System.out.println("Opción ingresada no válida.");
                return true;
            }
        }
    }
    
    private void createGuest() throws Exception{
        System.out.println("Ingrese el documento de la persona: ");
        String inputDocument = Utils.getReader().nextLine();
        Long document = Utils.getValidator().isValidLong("Documento", inputDocument);
        
        System.out.println("Ingrese el nombre de la persona: ");
        String inputName = Utils.getReader().nextLine();
        String name = Utils.getValidator().isValidString("Nombre", inputName);
        
        System.out.println("Ingrese el celular de la persona: ");
        String inputCellphone = Utils.getReader().nextLine();
        Long cellphone = Utils.getValidator().isValidLong("Celular", inputCellphone);
        
        System.out.println("Ingrese el nombre de usuario: ");
        String inputUserName = Utils.getReader().nextLine();
        String userName = Utils.getValidator().isValidString("Nombre de Usuario", inputUserName);
        
        System.out.println("Ingrese nueva contraseña: ");
        String inputPassword = Utils.getReader().nextLine();
        String password = Utils.getValidator().isValidString("Contraseña", inputPassword);
        
        PersonDto personDto = new PersonDto();
        personDto.setDocument(document);
        personDto.setName(name);
        personDto.setCellPhone(cellphone);
        
        UserDto userDto = new UserDto();
        userDto.setPersonId(personDto);
        userDto.setUserName(userName);
        userDto.setPassword(password);
        userDto.setRole(Role.GUEST);
        
        GuestDto guestDto = new GuestDto();
        guestDto.setUserId(userDto);
        guestDto.setStatus(GuestStatus.INACTIVE);
        
        this.service.createGuest(guestDto);
    }
    
    private void increaseAmount() throws Exception {
        PartnerDto currentPartner = this.service.getCurrentPartner();
        final double maxAmountVIP = 5000000;
        final double maxAmountRegular = 1000000;
        
        System.out.println("Ingrese el monto a adicionar a los fondos: ");
        String inputAddAmount = Utils.getReader().nextLine();
        Double addAmount = Utils.getValidator().isValidDouble("Fondos adicionales", inputAddAmount);
        
        double newAmount = currentPartner.getAmount() + addAmount;
        if((currentPartner.getType() == SubscriptionType.VIP && newAmount > maxAmountVIP) || 
            (currentPartner.getType() != SubscriptionType.VIP && newAmount > maxAmountRegular)){
            throw new Exception("Nuevo monto de fondos supera el máximo permitido \n"
                + "- VIP: " + maxAmountVIP + "\n"
                + "- REGULARES: " + maxAmountRegular + "\n");
        }
        
        newAmount = this.service.payPendingInvoices(newAmount);
        
        currentPartner.setAmount(newAmount);
        
        this.service.updatePartner(currentPartner);
        System.out.println("Fondos actualizados correctamente.\n");
    }
    
    private void requestVIPUpgrade() throws Exception {
        PartnerDto currentPartner = this.service.getCurrentPartner();
        currentPartner.setType(SubscriptionType.PENDING_VIP);
        this.service.updatePartner(currentPartner);
        System.out.println("La solicitud ha sido creada exitosamente.\n");
    }
    
    private void cancelSubscription() throws Exception {
        List<InvoiceDto> partnerInvoices = this.service.getPendingInvoicesByCurrentPartnerId();
        if(partnerInvoices.size() > 0){
            throw new Exception("No es posible cancelar suscripción, tiene facturas pendientes por pagar \n");
        }
        
        System.out.println("Eliminando datos del socio... \n");
        this.service.deleteCurrentUser();
        this.service.logout();
    }
    
    private void newService() throws Exception {
        System.out.println("¿Cuantos consumos desea realizar?");
        String inputCantProducts = Utils.getReader().nextLine();
        int cantProducts = Utils.getValidator().isValidInteger("Cantidad de productos ", inputCantProducts);
        InvoiceDetailDto details[] = new InvoiceDetailDto[cantProducts]; 
        double totalInvoice = 0;
        
        for (int i = 0; i < cantProducts; i++) {
            System.out.println("Producto # " + (i+1) + ": \n");
            System.out.println("Ingrese la descripción del producto consumido: ");
            String inputDesc = Utils.getReader().nextLine();
            String desc = Utils.getValidator().isValidString("Descripción", inputDesc);

            System.out.println("Ingrese precio del producto consumido: ");
            String inputAmount = Utils.getReader().nextLine();
            double amount = Utils.getValidator().isValidDouble("Precio", inputAmount);

            System.out.println("Ingrese cantidad del producto consumido: ");
            String inputItem = Utils.getReader().nextLine();
            int item = Utils.getValidator().isValidInteger("Cantidad", inputItem);
            
            totalInvoice += amount * item;
            
            InvoiceDetailDto invoiceDetailDto = new InvoiceDetailDto();
            invoiceDetailDto.setAmount(amount);
            invoiceDetailDto.setDescription(desc);
            invoiceDetailDto.setItem(item);
            
            details[i] = invoiceDetailDto;
        }
            
        Calendar calendar = Calendar.getInstance();
        Timestamp creationDate = new Timestamp(calendar.getTimeInMillis());
        PartnerDto currentPartner = this.service.getCurrentPartner();
        
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setAmount(totalInvoice);
        invoiceDto.setCreationDate(creationDate);
        invoiceDto.setStatus(InvoiceStatus.PENDING);
        invoiceDto.setPartnerId(currentPartner);        
        
        this.service.createInvoice(invoiceDto,details);
    }
    
    private void activateGuest() throws Exception {
        PartnerDto currentPartner = this.service.getCurrentPartner();
        List<GuestDto> guests = this.service.getGuestsByCurrentPartner();
        int currentActive = 0;
        
        System.out.println("Lista de Invitados del socio:");
        for(GuestDto guest : guests){
            System.out.println(guest.toString());
            if(guest.getStatus() == GuestStatus.ACTIVE){
                currentActive++;
            }
        }
        
        if(currentPartner.getType() != SubscriptionType.VIP && currentActive >= 3){
            throw new Exception("Excede límite de invitados activos permitido \n");
        }
        
        System.out.println("Ingrese el ID del invitado a activar: ");
        String idGuestToActivateInput = Utils.getReader().nextLine();
        Long idGuestToActivate = Utils.getValidator().isValidLong("ID Invitado", idGuestToActivateInput);
        
        for(GuestDto guest : guests){
            if(guest.getId() == idGuestToActivate && guest.getStatus() != GuestStatus.ACTIVE){
                guest.setStatus(GuestStatus.ACTIVE);
                this.service.updateGuest(guest);
                System.out.println("Invitado activado exitosamente");
            }else if(guest.getId() == idGuestToActivate){
                throw new Exception("Invitado ya se encuentra activado \n");
            }
        }
    }
    
    
    private void deactivateGuest() throws Exception {
        List<GuestDto> guests = this.service.getGuestsByCurrentPartner();
        
        System.out.println("Lista de Invitados del socio:");
        for(GuestDto guest : guests){
            System.out.println(guest.toString());
        }
        
        System.out.println("Ingrese el ID del invitado a desactivar: ");
        String idGuestToDeactivateInput = Utils.getReader().nextLine();
        Long idGuestToDeactivate = Utils.getValidator().isValidLong("ID Invitado", idGuestToDeactivateInput);
        
        for(GuestDto guest : guests){
            if(guest.getId() == idGuestToDeactivate && guest.getStatus() != GuestStatus.INACTIVE){
                guest.setStatus(GuestStatus.INACTIVE);
                this.service.updateGuest(guest);
                System.out.println("Invitado desactivado exitosamente");
            }else if(guest.getId() == idGuestToDeactivate){
                throw new Exception("Invitado ya se encuentra desactivado \n");
            }
        }
    }
    
    private void showInvoices() throws Exception {
        List<InvoiceDto> invoicesDto = this.service.getAllInvoicesByPartnerId();
        if(invoicesDto.size() < 1){
            System.out.println("No se encontraron datos.");
            return;
        }
        for(InvoiceDto invoiceDto : invoicesDto){
            System.out.println(invoiceDto.toString());
        }
    }
}
