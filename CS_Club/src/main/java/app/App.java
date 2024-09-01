
package app;

import app.config.DBConnection;
import app.controller.ControllerInterface;
import app.controller.LoginController;

public class App {

    public static void main(String[] args) {
        ControllerInterface controller = new LoginController();
        try {
            //DBConnection.getConnection();
            controller.session();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
