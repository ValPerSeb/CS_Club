
package app.dao;

import app.dao.repositories.PersonRepository;
import app.dto.PersonDto;
import app.model.Person;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Setter
@Getter
@Service
@NoArgsConstructor

public class PersonDao {
    
    @Autowired
    public PersonRepository personRepository;
    
    public boolean existsByDocument(PersonDto personDto) throws Exception {
        return personRepository.existsByDocument(personDto.getDocument());
    }

    public void createPerson(PersonDto personDto) throws Exception {
        Person person = Helper.parse(personDto);
        personRepository.save(person);
    }

    public void deletePerson(long id) throws Exception {
        personRepository.deleteById(id);	
    }

    public PersonDto findByDocument(PersonDto personDto) throws Exception {
        Person person = personRepository.findByDocument(personDto.getDocument());
        if(person == null){
            return null;
        }
        return Helper.parse(person);
    }
}
