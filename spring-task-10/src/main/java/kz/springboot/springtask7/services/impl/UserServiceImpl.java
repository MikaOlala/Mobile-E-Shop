package kz.springboot.springtask7.services.impl;

import kz.springboot.springtask7.entities.Comment;
import kz.springboot.springtask7.entities.Role;
import kz.springboot.springtask7.entities.Users;
import kz.springboot.springtask7.repositories.CommentRepository;
import kz.springboot.springtask7.repositories.ItemRepository;
import kz.springboot.springtask7.repositories.RoleRepository;
import kz.springboot.springtask7.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException{
        Users myUser = userRepository.findByEmail(s);
        if(myUser!=null) {
            User secUser = new User(myUser.getEmail(), myUser.getPassword(), myUser.getRoles());
            return secUser;
        }
        throw new UsernameNotFoundException("User Not Found");
    }

    @Override
    public Users getUsersByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<Users> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public Users getUser(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public Users addUser(Users user) {
        return userRepository.save(user);
    }
    @Override
    public Users editUser(Users user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Users user) {
        userRepository.delete(user);
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRole(Long id) {return roleRepository.getOne(id);}

    @Override
    public Role addRole(Role role) {return roleRepository.save(role);}

    @Override
    public Role editRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Role role) {roleRepository.delete(role);}

    @Override
    public List<Role> getUserRole(Long id) {
        return roleRepository.findAllById(id);
    }

    @Override
    public Users createUser(Users user) {
        Role role = roleRepository.findByRole("ROLE_USER");
        if(role!=null) {
            List<Role> roles = new ArrayList<>();
            roles.add(role);
            user.setRoles(roles);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public Users createUserByAdmin(Users user, boolean moderator, boolean admin) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        List<Role> roles = new ArrayList<>();
        if(moderator) {
            Role role = roleRepository.findByRole("ROLE_MODERATOR");
            roles.add(role);
        }
        if(admin) {
            Role role = roleRepository.findByRole("ROLE_ADMIN");
            roles.add(role);
        }
        Role role = roleRepository.findByRole("ROLE_USER");
        roles.add(role);
        System.out.println(roles);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public Users editUser(Users user, boolean moderator, boolean admin) {
        List<Role> roles = user.getRoles();
        if(moderator) {
            Role role = roleRepository.findByRole("ROLE_MODERATOR");
            if(!(roles.contains(role)))
                roles.add(role);
        }
        else {
            Role role = roleRepository.findByRole("ROLE_MODERATOR");
            if(roles.contains(role))
                roles.remove(role);
        }
        if(admin) {
            Role role = roleRepository.findByRole("ROLE_ADMIN");
            if(!(roles.contains(role)))
                roles.add(role);
        }
        else {
            Role role = roleRepository.findByRole("ROLE_ADMIN");
            if(roles.contains(role))
                roles.remove(role);
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public Users changePassword(Users user, String nw) {
        user.setPassword(bCryptPasswordEncoder.encode(nw));
        return userRepository.save(user);
    }

    @Override
    public Users diffPasswords(Users user, String old, String nw) {
        if(bCryptPasswordEncoder.matches(old, user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(nw));
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public List<Comment> getCommentByItem(Long id) {
        return commentRepository.getAllByItemId(id);
    }

    @Override
    public Comment getComment(Long id) {
        return commentRepository.getOne(id);
    }

    @Override
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment editComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }
}
