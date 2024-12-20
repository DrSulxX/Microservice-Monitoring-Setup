package microservice.search.repository;

import microservice.search.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository {
    // Täiendavad päringud või meetodid, kui vajad seda oma mikroteenuses
    List<User> findByEmailIgnoreCase(String email);
    List<User> findByUsernameContainingIgnoreCase(String username);
    List<User> findByNameContainingIgnoreCase(String name);
    List<User> findByFamilyNameContainingIgnoreCase(String familyName);
    // Võib lisada muid meetodeid vastavalt vajadusele
}