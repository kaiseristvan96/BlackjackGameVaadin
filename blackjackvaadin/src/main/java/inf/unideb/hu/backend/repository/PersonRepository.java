package inf.unideb.hu.backend.repository;

import inf.unideb.hu.backend.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
    PersonEntity findByEmail(String email);

    PersonEntity findByUsername(String username);

    @Query("SELECT p.score FROM Person p WHERE p.username = ?1")
    Long findScoreByUsername(String username);
}
