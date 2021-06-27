package kz.springboot.springtask7.repositories;

import kz.springboot.springtask7.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> getAllByItemId(Long id);
}
