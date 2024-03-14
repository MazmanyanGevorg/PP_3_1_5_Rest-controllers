package ru.kata.spring.boot_security.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kata.spring.boot_security.demo.model.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long id;
    private  String role;

    public RoleDTO(Role role) {
        this.id = role.getId();
        this.role = role.getRole();
    }

    public RoleDTO(Long id) {
        this.id = id;
    }
}
