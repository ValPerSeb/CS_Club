
package app.dao;

import app.config.DBConnection;
import app.dto.PersonDto;
import app.model.Person;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;

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

    public void deletePerson(long[] ids) throws Exception {
        if (ids == null || ids.length == 0) {
            throw new Exception("Error en la lista de IDS.");
        }
        String placeholders = String.join(",", Collections.nCopies(ids.length, "?"));
        String query = "DELETE FROM PERSON WHERE ID IN (" + placeholders + ")";
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
        for (int i = 0; i < ids.length; i++) {
            preparedStatement.setLong(i + 1, ids[i]);
        }
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
