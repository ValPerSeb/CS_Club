
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
            + "\n 7. Solicitar promoción a VIP. \n"
            + "\n 8. Cerrar Sesión. \n";

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
                System.out.println("***Activación de Invitado***"); //TODO
                return true;
            }
            case "3": {
                System.out.println("***Desactivación de Invitado***"); //TODO
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
                return true;
            }
            case "7": {
                System.out.println("***Solicitar Promoción a VIP***");
                this.requestVIPUpgrade();
                return true;
            }
            case "8": {
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
        
        PartnerDto currentPartner = this.service.getCurrentPartner();
        
        GuestDto guestDto = new GuestDto();
        guestDto.setUserId(userDto);
        guestDto.setPartnerId(currentPartner);
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
        currentPartner.setAmount(newAmount);
        
        this.service.updatePartner(currentPartner);
        System.out.println("Fondos actualizados correctamente.");
    }
    
    private void requestVIPUpgrade() throws Exception {
        PartnerDto currentPartner = this.service.getCurrentPartner();
        currentPartner.setType(SubscriptionType.PENDING_VIP);
        this.service.updatePartner(currentPartner);
        System.out.println("La solicitud ha sido creada exitosamente.");
    }
    
    private void cancelSubscription() throws Exception {
        List<InvoiceDto> partnerInvoices = this.service.getPendingInvoicesByCurrentPartnerId();
        if(partnerInvoices.size() > 0){
            throw new Exception("No es posible cancelar suscripción, tiene facturas pendientes por pagar \n");
        }
        System.out.println("Eliminando facturas del socio...");
        this.service.deleteInvoicesByCurrentPartnerId();
        
        System.out.println("Eliminando Invitados del socio...");
        this.service.deleteGuestsByCurrentPartnerId();
        
        System.out.println("Eliminando datos del socio...");
        this.service.deleteCurrentPartner();
    }
    
    private void newService() throws Exception {
        System.out.println("Ingrese la descripción del producto consumido: ");
        String inputDesc = Utils.getReader().nextLine();
        String desc = Utils.getValidator().isValidString("Descripción", inputDesc);
        
        System.out.println("Ingrese precio del producto consumido: ");
        String inputAmount = Utils.getReader().nextLine();
        double amount = Utils.getValidator().isValidDouble("Precio", inputAmount);
        
        System.out.println("Ingrese cantidad del producto consumido: ");
        String inputItem = Utils.getReader().nextLine();
        int item = Utils.getValidator().isValidInteger("Cantidad", inputItem);
        
        InvoiceDetailDto invoiceDetailDto = new InvoiceDetailDto();
        invoiceDetailDto.setAmount(amount);
        invoiceDetailDto.setDescription(desc);
        invoiceDetailDto.setItem(item);
        
        double totalInvoice = amount * item;
        Calendar calendar = Calendar.getInstance();
        Timestamp creationDate = new Timestamp(calendar.getTimeInMillis());
        PartnerDto currentPartner = this.service.getCurrentPartner();
        
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setAmount(totalInvoice);
        invoiceDto.setCreationDate(creationDate);
        invoiceDto.setStatus(InvoiceStatus.PENDING);
        invoiceDto.setPartnerId(currentPartner);
        
        invoiceDetailDto.setInvoiceId(invoiceDto);
        
        this.service.createInvoice(invoiceDetailDto);
    }
}
