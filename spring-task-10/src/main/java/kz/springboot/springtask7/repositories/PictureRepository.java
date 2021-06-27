package kz.springboot.springtask7.repositories;

import kz.springboot.springtask7.entities.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    List<Picture> findAllByItemId(Long id);
    List<Picture> findAllByItemIsNull();
}
