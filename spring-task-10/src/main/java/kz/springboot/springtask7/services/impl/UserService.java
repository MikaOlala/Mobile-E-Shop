package kz.springboot.springtask7.services.impl;

import kz.springboot.springtask7.entities.Comment;
import kz.springboot.springtask7.entities.Role;
import kz.springboot.springtask7.entities.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    Users getUsersByEmail(String email);
    List<Users> getUsers();
    Users getUser(Long id);
    Users addUser(Users user);
    Users editUser(Users user);
    Users editUser(Users user, boolean moderator, boolean admin);
    void deleteUser(Users user);
    List<Role> getRoles();
    Role getRole(Long id);
    Role addRole(Role role);
    Role editRole(Role role);
    void deleteRole(Role role);
    List<Role> getUserRole(Long id);

    Users createUser(Users user);
    Users createUserByAdmin(Users user, boolean moderator, boolean admin);
    Users changePassword(Users user, String nw);
    Users diffPasswords(Users user, String old, String nw);

    List<Comment> getCommentByItem(Long id);
    Comment getComment(Long id);
    Comment addComment(Comment comment);
    Comment editComment(Comment comment);
    void deleteComment(Comment comment);
}
