package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dao.RoleDAO;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/")
public class AdminController {
    private final UserServiceImpl userService;
    private final RoleDAO roleDAO;
    @Autowired
    public AdminController(UserServiceImpl userService, RoleDAO roleDAO) {
        this.userService = userService;
        this.roleDAO = roleDAO;
    }
///////////////////////////////////////login////////////////////////////////////////////////

    @RequestMapping("/login")
    public String login() {
        return "/login";
    }

    // Login form with error
    @RequestMapping("/login_error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "/login";
    }

///////////////////////////////////////admin////////////////////////////////////////////////

    @GetMapping(value = "")
    public String getUsersListForm(Model model, Principal principal) {
        final String principalName = principal == null ? "aaaa" : principal.getName();
        final User user = userService.getUserByName(principalName);
        if (user == null) {
            throw new UsernameNotFoundException("User with principalName: " + principalName + " not found");
        }
        model.addAttribute("user", new User());
        model.addAttribute("roles1", roleDAO.findAll());
        model.addAttribute("users1", user); // с данными user'а
        model.addAttribute("users", userService.getAllUsers());
        return "/code-basics";
    }

    //////////////////////////////edit/update/showById//////////////////////////////////
    @PostMapping("/edit")
    public String updateUser(@ModelAttribute User user,
                             BindingResult bindingResult, Model model) {
        System.out.println("-------------------------------------------------------UserController.updateUser");
        System.out.println("-------------------------------------------------------user = " + user + ", bindingResult = " + bindingResult + ", model = " + model);
        if (bindingResult.hasErrors()) {
            model.addAttribute("userEdit", user);
            model.addAttribute("rolesEdit", roleDAO.findAll());
            return "/code-basics";
        }
        userService.updateUser(user);
        return "redirect:/";
    }

    ////////////////////////////////create new user//////////////////////////////////////

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public String addUser(@ModelAttribute("user") @Valid User user,
                          BindingResult bindingResult, Model model) {
        userService.conditionForBindingResult(bindingResult);
        userService.addUser(user);
        return "redirect:/";
    }


/////////////////////////////////delete///////////////////////////////////////////////

    @PostMapping("")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/";
    }
}
