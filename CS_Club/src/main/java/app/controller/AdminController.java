
package app.controller;

public class AdminController implements ControllerInterface{
    private static final String MENU = "Ingrese la opción deseada: \n 1. Crear nuevo socio. "
            + "\n 2. Ver historial de facturas Club. "
            + "\n 3. Ver historial de facturas Socio. "
            + "\n 4. Ver historial de facturas Invitado. "
            + "\n 5. Ejecutar promoción a VIP. "
            + "\n 6. Cerrar Sesión.\n";

    public AdminController(){
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
                    System.out.println("***Nuevo socio***\n"); //TODO
                    return true;
            }
            case "2": {
                    System.out.println("***Historial de facturas Club***\n"); //TODO
                    return true;
            }
            case "3": {
                    System.out.println("***Historial de facturas Socio***\n"); //TODO
                    return true;
            }
            case "4": {
                    System.out.println("***Historial de facturas Invitado***\n");
                    return true;
            }
            case "5": {
                    System.out.println("***Promoción a VIP***\n");
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
}
