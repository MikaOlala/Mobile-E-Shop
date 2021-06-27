package kz.springboot.springtask7.repositories;

import kz.springboot.springtask7.entities.Role;
import kz.springboot.springtask7.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<Users, Long> {

    Users findByEmail(String email);
}
