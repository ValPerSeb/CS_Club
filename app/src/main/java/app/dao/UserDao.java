
package app.dao;


import app.dao.repositories.UserRepository;
import app.dto.UserDto;
import app.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@NoArgsConstructor

public class UserDao {
    
    @Autowired
    public UserRepository userRepository;
    
    public UserDto findByUserName(UserDto userDto) throws Exception {
        User user = userRepository.findByUserName(userDto.getUserName());
        if(user == null){
            return null;
        }
        return Helper.parse(user);
    }
    
    public boolean existsByUserName(UserDto userDto) throws Exception {
        return userRepository.existsByUserName(userDto.getUserName());
    }
    
    public void createUser(UserDto userDto) throws Exception {
        User user = Helper.parse(userDto);
        userRepository.save(user);
    }
    
    public void updateUser(UserDto userDto)throws Exception{
        User user = userRepository.findByUserName(userDto.getUserName());
        if (user == null) {
            throw new Exception("Usuario no encontrado.");
        }
        user.setRole(userDto.getRole());
        userRepository.save(user);
    }
}