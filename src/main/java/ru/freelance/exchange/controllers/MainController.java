package ru.freelance.exchange.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.freelance.exchange.dao.ServicesDAO;
import ru.freelance.exchange.dao.UsersDAO;
import ru.freelance.exchange.models.Services;
import ru.freelance.exchange.models.Users;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/")
public class MainController {

    private final UsersDAO usersDAO;

    @Autowired
    public MainController(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    @GetMapping()
    public String index(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        model.addAttribute("user", usersDAO.findByUsername(username));

        return "main/index";
    }

    // Страница регистрации
    @GetMapping("/registration")
    public String index(@ModelAttribute("user") @Valid Users user) {
        return "registration/index";
    }

    @PostMapping("/registration")
    public String new_user(@ModelAttribute("user") @Valid Users user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors())
            return"registration/index";

        if (!usersDAO.isEmailUnique(user.getEmail())) {
            model.addAttribute("emailError", true);
        }

        if (!user.isPasswordsEqual()) {
            model.addAttribute("passwordError", true);
        }

        if (model.containsAttribute("emailError") || model.containsAttribute("passwordError"))
            return "registration/index";

        usersDAO.new_user(user);

        return "redirect:login/index";
    }

}
