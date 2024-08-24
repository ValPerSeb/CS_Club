
package app.service;

import app.dao.UserDao;
import app.dto.UserDto;

public class UserService {
    private UserDao userDao;
    public static UserDto user;
    
    public UserService() {
        this.userDao = new UserDao();
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
}
