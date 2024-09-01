
package app.dao;

import app.config.DBConnection;
import app.dto.UserDto;
import app.model.Person;
import app.model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import app.model.Role;

public class UserDao {
    public UserDto findByUserName(UserDto userDto) throws Exception {
        String query = "SELECT ID,USERNAME,PASSWORD,ROLE,PERSONNID FROM USER WHERE USERNAME = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setString(1, userDto.getUserName());
        ResultSet resulSet = preparedStatement.executeQuery();
        if (resulSet.next()) {
            User user = new User();
            user.setId(resulSet.getLong("ID"));
            user.setUserName(resulSet.getString("USERNAME"));
            user.setPassword(resulSet.getString("PASSWORD"));;
            user.setRole(Role.valueOf(resulSet.getString("ROLE")));
            Person person = new Person();
            person.setDocument(resulSet.getLong("PERSONNID"));
            user.setPersonId(person);
            resulSet.close();
            preparedStatement.close();
            return Helper.parse(user);
        }
        resulSet.close();
        preparedStatement.close();
        return null;
    }
    
    public boolean existsByUserName(UserDto userDto) throws Exception {
        String query = "SELECT 1 FROM USER WHERE USERNAME = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setString(1, userDto.getUserName());
        ResultSet resulSet = preparedStatement.executeQuery();
        boolean exists = resulSet.next();
        resulSet.close();
        preparedStatement.close();
        return exists;
    }
    
    public void createUser(UserDto userDto) throws Exception {
        User user = Helper.parse(userDto);
        String query = "INSERT INTO USER(USERNAME,PASSWORD,PERSONNID,ROLE) VALUES (?,?,?,?) ";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setString(1, user.getUserName());
        preparedStatement.setString(2, user.getPassword());
        preparedStatement.setLong(3,user.getPersonId().getId());
        preparedStatement.setString(4, user.getRole().toString());
        preparedStatement.execute();
        preparedStatement.close();
    }
    
    public void deleteUser(UserDto userDto) throws Exception {
        User user = Helper.parse(userDto);
        String query = "DELETE FROM USER WHERE ID = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setLong(1,user.getId());
        preparedStatement.execute();
        preparedStatement.close();	
    }
}
