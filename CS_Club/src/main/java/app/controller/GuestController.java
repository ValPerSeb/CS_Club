
package app.controller;

public class GuestController implements ControllerInterface{
    private static final String MENU = "Ingrese la opción deseada: \n 1. Hacer consumo. "
            + "\n 2. Solicitud ascenso a Socio. "
            + "\n 3. Cerrar sesión. \n";

    public GuestController(){
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
                System.out.println("***Nuevo Consumo***"); //TODO
                return true;
            }
            case "2": {
                System.out.println("***Solicitud ascenso a Socio***"); //TODO
                return true;
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
}
