package kz.springboot.springtask7.repositories;

import kz.springboot.springtask7.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOrderByTopDesc();
    List<Item> findAllByNameLikeOrderByPriceAsc(String name);
    List<Item> findAllByNameLikeOrderByPriceDesc(String name);
    List<Item> findAllByNameLikeAndPriceBetweenOrderByPriceAsc(String name, int from, int to);
    List<Item> findAllByNameLikeAndPriceBetweenOrderByPriceDesc(String name, int from, int to);

    List<Item> findAllByNameLikeAndBrandIdOrderByPriceAsc(String name, Long id);
    List<Item> findAllByNameLikeAndBrandIdOrderByPriceDesc(String name, Long id);
    List<Item> findAllByNameLikeAndBrandIdAndPriceBetweenOrderByPriceAsc(String name, Long id, int from, int to);
    List<Item> findAllByNameLikeAndBrandIdAndPriceBetweenOrderByPriceDesc(String name, Long id, int from, int to);

}
