
package app.controller;

public class PartnerController implements ControllerInterface{
    private static final String MENU = "Ingrese la opción deseada: \n 1. Crear Invitado. "
            + "\n 2. Activar Invitado. "
            + "\n 3. Desactivar Invitado. "
            + "\n 4. Hacer consumo. "
            + "\n 5. Subir fondos. "
            + "\n 6. Solicitar baja. "
            + "\n 7. Solicitar promoción a VIP. \n"
            + "\n 8. Cerrar Sesión. \n";

    public PartnerController(){
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
                System.out.println("***Nuevo Invitado***\n"); //TODO
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
                return true;
            }
            case "5": {
                System.out.println("***Subir fondos***");
                return true;
            }
            case "6": {
                    System.out.println("***Solicitar baja***");
                    return true;
            }
            case "7": {
                    System.out.println("***Solicitar Promoción a VIP***");
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
}
