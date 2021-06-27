package kz.springboot.springtask7.repositories;

import kz.springboot.springtask7.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllById(Long id);
    Role findByRole(String role);
}
