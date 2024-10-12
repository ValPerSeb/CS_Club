
package app.controller;

import app.dto.GuestDto;
import app.dto.InvoiceDetailDto;
import app.dto.InvoiceDto;
import app.dto.PartnerDto;
import app.model.GuestStatus;
import app.model.InvoiceStatus;
import app.model.SubscriptionType;
import app.service.ClubService;
import static app.service.ClubService.user;
import java.sql.Timestamp;
import java.util.Calendar;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@Getter
@Setter
@NoArgsConstructor

public class GuestController implements ControllerInterface{
    @Autowired
    private ClubService service;
    private static final String MENU = "Ingrese la opción deseada: \n 1. Hacer consumo. "
            + "\n 2. Solicitud ascenso a Socio. "
            + "\n 3. Cerrar sesión. \n";  
    
    @Override
    public void session() throws Exception {
        boolean session = true;
        while (session) {
            session = menu();
        }
    }
    
    private boolean menu() {
        try {
            System.out.println("*** Bienvenido ***"); // Service.user.getUserName()
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
                System.out.println("***Nuevo Consumo***");
                this.newServiceForInvited();
                return true;
            }
            case "2": {
                System.out.println("***Solicitud ascenso a Socio***");
                this.partnerUpgrade();
                return!(user == null);
            }
            case "3": {
                System.out.println("Se ha cerrado sesión.");
                return false;
            }
            default: {
                System.out.println("Opción ingresada no válida.");
                return true;
            }
        }
    }
    
    private void newServiceForInvited() throws Exception {
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
        GuestDto currentGuest = this.service.getCurrentGuest();
        
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setAmount(totalInvoice);
        invoiceDto.setCreationDate(creationDate);
        invoiceDto.setStatus(InvoiceStatus.PENDING);
        invoiceDto.setPartnerId(currentGuest.getPartnerId());        
        
        this.service.createInvoice(invoiceDto,details);
    }
    
    private void partnerUpgrade() throws Exception {
        GuestDto currentGuest = this.service.getCurrentGuest();
        PartnerDto partnerDto = new PartnerDto();
        partnerDto.setAmount(50000);
        partnerDto.setType(SubscriptionType.REGULAR);
        Calendar calendar = Calendar.getInstance();
        Timestamp creationDate = new Timestamp(calendar.getTimeInMillis());
        partnerDto.setCreationDate(creationDate);
        partnerDto.setUserId(currentGuest.getUserId());
        
        this.service.createPartnerFromGuest(partnerDto);
        
        System.out.println("La conversión ha socio ha sido realizada exitosamente.\n");
        
        this.service.logout();
    }
}
