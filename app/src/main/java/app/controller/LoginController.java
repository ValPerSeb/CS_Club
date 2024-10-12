
package app.controller;

import app.dto.UserDto;
import app.model.Role;
import app.service.ClubService;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@Getter
@Setter


public class LoginController implements ControllerInterface{
    @Autowired
    private ClubService service;
    private static final String MENU= "*** CS Club *** \n Ingrese la opción deseada: \n 1. Iniciar Sesión. \n 2. Salir. \n";
    private Map<Role,ControllerInterface> roles;

    public LoginController(ControllerInterface adminController,PartnerController partnerController,GuestController guestController) {
        this.service = new ClubService();
        this.roles= new HashMap<Role,ControllerInterface>();
        roles.put(Role.ADMIN, adminController);
        roles.put(Role.PARTNER, partnerController);
        roles.put(Role.GUEST, guestController);
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
            System.out.println(MENU);
            String option = Utils.getReader().nextLine();
            return options(option);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return true;
        }
    }
    
    private boolean options(String option) throws Exception {
        switch (option) {
            case "1": {
                    this.login();
                    return true;
            }
            case "2": {
                    System.out.println("Saliendo del programa...");
                    return false;
            }
            default: {
                    System.out.println("Ingrese una opción válida");
                    return true;
            }
        }
    }
	
    private void login()throws Exception {
        System.out.println("Ingrese el Usuario");
        String inputUserName= Utils.getReader().nextLine();
        String userName = Utils.getValidator().isValidString("Usuario", inputUserName);
        
        System.out.println("Ingrese la Contraseña");
        String inputPassword= Utils.getReader().nextLine();
        String password = Utils.getValidator().isValidString("Contraseña" ,inputPassword);
        
        UserDto userDto = new UserDto();
        userDto.setPassword(password);
        userDto.setUserName(userName);
        
        this.service.login(userDto);
        if(roles.get(userDto.getRole())==null) {
            throw new Exception ("Rol invalido");
        }
        
        roles.get(userDto.getRole()).session();
    }
}
