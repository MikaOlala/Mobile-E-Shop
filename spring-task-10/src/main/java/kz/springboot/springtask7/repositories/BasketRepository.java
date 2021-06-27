package kz.springboot.springtask7.repositories;

import kz.springboot.springtask7.entities.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Long> {
}
