package ru.kata.spring.boot_security.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String name;

    private String surname;

    @Size(min = 2, message = "Не меньше 2 знаков")
    private String password;

    private int age;

    private int level;

    private int points;

    private Set<RoleDTO> roles;
//    private List<String> roles1 = new ArrayList<>();


    public UserDTO(User user) {

        this.id = user.getId();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.password = user.getPassword();
        this.age = user.getAge();
        this.level = user.getLevel();
        this.points = user.getPoints();
//        this.roles = user.getRoles().stream().map(Role::getId).map(RoleDTO::new).collect(Collectors.toSet());
    }
}
