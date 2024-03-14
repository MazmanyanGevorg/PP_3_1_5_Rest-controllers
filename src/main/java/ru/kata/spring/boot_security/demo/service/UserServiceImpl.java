package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.kata.spring.boot_security.demo.dao.RoleDAO;
import ru.kata.spring.boot_security.demo.dao.UserDAO;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.util.UserNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    @PersistenceContext
    private EntityManager entityManager;
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    public UserServiceImpl(UserDAO userDAO, RoleDAO roleDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @Override
    @Transactional
    public void addUser(User user) {
        User userByIdFromDB = userDAO.getUserById(user.getId());

        if (userByIdFromDB == null) {
            createRolesIfNotExist();
            user.setPassword(passwordEncoder.encode(user.getPassword()));
//            enrichUser(user);
            userDAO.saveAndFlush(user);
        } else throw new RuntimeException("User by id: " + user.getId() + " in DB already exist");
    }

//    private void enrichUser(User user) {
//        user.setCreatedAt(LocalDateTime.now());
//        user.setUpdateAt(LocalDateTime.now());
//        user.setCreatedWho("ADMIN");
//    }

    @Override
    public void updateUser(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        entityManager.merge(user);
        userDAO.save(user);
    }

    @Override
    public User getUserById(Long id) {
        User user = userDAO.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("There is no user with ID = " +
                    id + " in Database");
        }
        return userDAO.getUserById(id);
    }

    @Override
    public User getUserByName(String name) {
        return userDAO.findByName(name);
    }

    @Override
    @Transactional
    public void createRolesIfNotExist() {
        if (roleDAO.findById(1L).isEmpty()) {
            roleDAO.save(new Role(1L, "ROLE_ADMIN"));
        }
        if (roleDAO.findById(2L).isEmpty()) {
            roleDAO.save(new Role(2L, "ROLE_USER"));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getListOfRoles() {
        return roleDAO.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userDAO.findByName(name);

        if (user == null) {
            throw new UsernameNotFoundException("User " + name + " not found");
        }
        return new org.springframework.security.core.userdetails.
                User(user.getUsername(), user.getPassword(), user.getRoles());
    }

    @Override
    public Set<GrantedAuthority> getAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getRole()));
        }
        return authorities;
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        userDAO.deleteUserById(id);
    }

    @Override
    @Transactional
    public void deleteUserByName(String name) {
        User user = getUserByName(name);
        if (user != null) {
            userDAO.deleteUserByName(name);
            entityManager.remove(user);
        }
    }

    public void conditionForBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }
            throw new UsernameNotFoundException(errorMsg.toString());
        }
    }
}
