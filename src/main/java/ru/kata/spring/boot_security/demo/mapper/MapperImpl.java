package ru.kata.spring.boot_security.demo.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.RoleDAO;
import ru.kata.spring.boot_security.demo.dto.RoleDTO;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MapperImpl implements Mapper {

    private final RoleDAO roleDAO;

    @Override
    public UserDTO convertToUserDTO(User user) {
        Set<RoleDTO> rolesDTO = user.getRoles().stream().map(RoleDTO::new).collect(Collectors.toSet());

        UserDTO userDTO = new UserDTO(user);
        userDTO.setRoles(rolesDTO);
        return userDTO;
    }

    @Override
    public User convertToUser(UserDTO userDTO) {
        Map<Long, Role> roleMap = roleDAO.findAll().stream().collect(Collectors.toMap(Role::getId, Function.identity()));
        Set<Role> roles = userDTO.getRoles().stream().map(RoleDTO::getId).map(roleMap::get).collect(Collectors.toSet());
        User user = new User(userDTO);
        user.setRoles(roles);
        return user;
    }
}
