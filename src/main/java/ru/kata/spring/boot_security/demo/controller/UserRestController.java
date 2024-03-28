package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.mapper.Mapper;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private final UserServiceImpl userService;

    private final Mapper mapper;

    @Autowired
    public UserRestController(UserServiceImpl userService, Mapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping(value = "/")
    public List<UserDTO> getUsersDTO() {
        List<User> users = userService.getAllUsers();
        return users.stream().map(mapper::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return mapper.convertToUserDTO(userService.getUserById(id));
    }

    @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        userService.conditionForBindingResult(bindingResult);
        userService.addUser(mapper.convertToUser(userDTO));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(value = "/", consumes = "application/json", produces = "application/json")
    public void edit(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        userService.conditionForBindingResult(bindingResult);
        userService.updateUser(mapper.convertToUser(userDTO));
    }

    @DeleteMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public String deleteUser(@PathVariable Long id) {
        userService.getUserById(id);
        userService.deleteUserById(id);
        return "User with ID = " + id + " was deleted";
    }

}


