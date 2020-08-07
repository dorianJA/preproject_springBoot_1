package ru.jm.springboot2.springboot2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.jm.springboot2.springboot2.model.Role;
import ru.jm.springboot2.springboot2.model.User;
import ru.jm.springboot2.springboot2.service.UserService;
import ru.jm.springboot2.springboot2.validator.UserValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class AdminController {

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/admin/users")
    public String tableUsers(ModelMap model,@AuthenticationPrincipal UserDetails user) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("newUser", new User());
        model.addAttribute("listUser", users);
        //add roles and name
        Set<Role> roles = (Set<Role>) user.getAuthorities();
        List<String> list = new ArrayList<>();
        for (Role r : roles) {
            list.add(r.getName());
        }
        model.addAttribute("usName",user.getUsername());
        model.addAttribute("roles",list);
        return "users";
    }

    @PostMapping(value = "/admin/edit")
    public String editUsers(@Valid @ModelAttribute("userData") User userData, BindingResult result) {
        User user = userService.getUserById(userData.getId());
        if(result.hasErrors()){
            return "editForm";
        }
        userData.setRoles(user.getRoles());
        userService.updateUser(userData);
        return "redirect:/admin/users";
    }

    @GetMapping(value = "/admin/edit")
    public String editUsers(@RequestParam("id") Long id, ModelMap model) {
        User user = userService.getUserById(id);
        model.addAttribute("userData", user);
        return "editForm";
    }

    @GetMapping(value = "/admin/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.removeUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping(value = "/admin/add")
    public String addUser(@Valid @ModelAttribute("newUser") User newUser, BindingResult result, Model model) {

        userValidator.validate(newUser,result);

        if (result.hasErrors()) {
            List<User> users = userService.getAllUsers();
            model.addAttribute("listUser", users);
            return "users";
        }
        userService.addUser(newUser);
        return "redirect:/admin/users";
    }
}
