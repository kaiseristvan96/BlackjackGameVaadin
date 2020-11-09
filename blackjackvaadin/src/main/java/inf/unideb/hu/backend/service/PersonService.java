package inf.unideb.hu.backend.service;


import inf.unideb.hu.backend.entity.PersonEntity;
import inf.unideb.hu.backend.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class PersonService {

    private static final Logger LOGGER = Logger.getLogger(PersonService.class.getName());
    private PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<PersonEntity> findAll() {
        return personRepository.findAll();
    }

    public PersonEntity findByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    public PersonEntity findByUsername(String username) {
        return personRepository.findByUsername(username);
    }

    public Long findScoreByUsername(String username) {
        return personRepository.findScoreByUsername(username);
    }

    public long count() {
        return personRepository.count();
    }

    public void delete(PersonEntity personEntity) {
        personRepository.delete(personEntity);
    }

    public void save(PersonEntity personEntity) {
        if (personEntity == null) {
            LOGGER.log(Level.SEVERE,
                    "Person is null. Contact support!");
            return;
        }
        personRepository.save(personEntity);
    }

}
