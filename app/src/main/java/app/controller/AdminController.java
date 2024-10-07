
package app.controller;

import app.dto.InvoiceDto;
import app.dto.PartnerDto;
import app.dto.PersonDto;
import app.dto.UserDto;
import app.model.Role;
import app.model.SubscriptionType;
import app.service.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class AdminController implements ControllerInterface{
    private Service service;
    private static final String MENU = "Ingrese la opción deseada: \n 1. Crear nuevo socio. "
            + "\n 2. Ver historial de facturas Club. "
            + "\n 3. Ver historial de facturas Socio. "
            + "\n 4. Ver historial de facturas Invitado. "
            + "\n 5. Ejecutar promoción a VIP. "
            + "\n 6. Cerrar Sesión.\n";

    public AdminController(){
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
                    System.out.println("***Nuevo socio***\n");
                    this.createPartner();
                    return true;
            }
            case "2": {
                    System.out.println("***Historial de facturas Club***\n");
                    this.invoicesHistory();
                    return true;
            }
            case "3": {
                    System.out.println("***Historial de facturas Socio***\n");
                    this.invoicesHistoryByRole(Role.PARTNER);
                    return true;
            }
            case "4": {
                    System.out.println("***Historial de facturas Invitado***\n");
                    this.invoicesHistoryByRole(Role.GUEST);
                    return true;
            }
            case "5": {
                    System.out.println("***Promoción a VIP de socios***\n");
                    this.upgradePartner();
                    return true;
            }
            case "6": {
                    System.out.println("Se ha cerrado sesión.\n");
                    return false;
            }
            default: {
                    System.out.println("Opción ingresada no válida.\n");
                    return true;
            }
        }
    }
    
    
    private void createPartner() throws Exception{
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
        userDto.setRole(Role.PARTNER);
        
        PartnerDto partnerDto = new PartnerDto();
        partnerDto.setAmount(50000);
        partnerDto.setType(SubscriptionType.REGULAR);
        
        Calendar calendar = Calendar.getInstance();
        Timestamp creationDate = new Timestamp(calendar.getTimeInMillis());
        partnerDto.setCreationDate(creationDate);
        partnerDto.setUserId(userDto);
        
        this.service.createPartner(partnerDto);
    }
    
    private void invoicesHistory() throws Exception{
        List<InvoiceDto> invoicesDto = this.service.getAllInvoices();
        if(invoicesDto.size() < 1){
            System.out.println("No se encontraron datos.");
            return;
        }
        for(InvoiceDto invoiceDto : invoicesDto){
            System.out.println(invoiceDto.toString());
        }
    }
    
    private void invoicesHistoryByRole(Role role) throws Exception{
        List<InvoiceDto> invoicesDto = this.service.getInvoicesByRole(role);
        if(invoicesDto.size() < 1){
            System.out.println("No se encontraron datos.");
            return;
        }
        for(InvoiceDto invoiceDto : invoicesDto){
            System.out.println(invoiceDto.toString());
        }
    }
    
    private void upgradePartner() throws Exception{
        List<PartnerDto> partnersDtoPending = this.service.getPartnersByType(SubscriptionType.PENDING_VIP);
        List<PartnerDto> partnersDtoVIP = this.service.getPartnersByType(SubscriptionType.VIP);
        
        int maxVIP = 5;
        int VIPCurrent = partnersDtoVIP.size();
        int pending = partnersDtoPending.size();
        int available = maxVIP - VIPCurrent;
        
        if(pending < 1){
            throw new Exception("No hay solicitudes pendientes.");
        }
        if(available <= 0){
            throw new Exception("Se ha superado el límite máximo de clientes VIP");
        }
        
        System.out.println("Solicitudes pendientes: ");
        for(PartnerDto partnerDto : partnersDtoPending){
            System.out.println(partnerDto.toString());
        }
        
        partnersDtoPending.sort(Comparator.comparingDouble(PartnerDto::getTotalInvoicesAmountPaid).reversed()
            .thenComparingDouble(PartnerDto::getAmount).reversed()
            .thenComparing(PartnerDto::getCreationDate));
        
        for (int i=0; i<available && i<pending; i++){
            PartnerDto selectedPartner = partnersDtoPending.get(i);
            System.out.println("Socio a promover a VIP es: ");
            System.out.println(selectedPartner.toString());

            selectedPartner.setType(SubscriptionType.VIP);
            this.service.updatePartner(selectedPartner);
            System.out.println("Socio actualizado a VIP exitosamente.\n");   
        }
    }
}
