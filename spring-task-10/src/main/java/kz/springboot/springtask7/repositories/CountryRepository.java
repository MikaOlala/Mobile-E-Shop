package kz.springboot.springtask7.repositories;

import kz.springboot.springtask7.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface CountryRepository extends JpaRepository<Country, Long> {

}
