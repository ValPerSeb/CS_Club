
package app.dao;

import app.config.DBConnection;
import app.dto.PersonDto;
import app.model.Person;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PersonDao {
    public boolean existsByDocument(PersonDto personDto) throws Exception {
        String query = "SELECT 1 FROM PERSON WHERE DOCUMENT = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setLong(1, personDto.getDocument());
        ResultSet resulSet = preparedStatement.executeQuery();
        boolean exists = resulSet.next();
        resulSet.close();
        preparedStatement.close();
        return exists;
    }

    public void createPerson(PersonDto personDto) throws Exception {
        Person person = Helper.parse(personDto);
        String query = "INSERT INTO PERSON(NAME,DOCUMENT,CELLPHONE) VALUES (?,?,?) ";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setString(1, person.getName());
        preparedStatement.setLong(2,person.getDocument());
        preparedStatement.setLong(3, person.getCellPhone());
        preparedStatement.execute();
        preparedStatement.close();
    }

    public void deletePerson(PersonDto personDto) throws Exception {
        Person person = Helper.parse(personDto);
        String query = "DELETE FROM PERSON WHERE DOCUMENT = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setLong(1,person.getDocument());
        preparedStatement.execute();
        preparedStatement.close();	
    }

    public PersonDto findByDocument(PersonDto personDto) throws Exception {
        String query = "SELECT ID,NAME,DOCUMENT,CELLPHONE FROM PERSON WHERE DOCUMENT = ?";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        preparedStatement.setLong(1, personDto.getDocument());
        ResultSet resulSet = preparedStatement.executeQuery();
        if (resulSet.next()) {
            Person person = new Person();
            person.setId(resulSet.getLong("ID"));
            person.setName(resulSet.getString("NAME"));
            person.setDocument(resulSet.getLong("DOCUMENT"));
            person.setCellPhone(resulSet.getLong("CELLPHONE"));
            resulSet.close();
            preparedStatement.close();
            return Helper.parse(person);
        }
        resulSet.close();
        preparedStatement.close();
        return null;
    }
}
