package ru.freelance.exchange.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.freelance.exchange.dao.CategoriesDAO;
import ru.freelance.exchange.dao.OrdersDAO;
import ru.freelance.exchange.dao.UsersDAO;

@Controller
@RequestMapping("/users")
public class UsersController {
    private final UsersDAO usersDAO;
    private final OrdersDAO ordersDAO;

    @Autowired
    public UsersController(UsersDAO usersDAO, OrdersDAO ordersDAO) {
        this.usersDAO = usersDAO;
        this.ordersDAO = ordersDAO;
    }

    @GetMapping
    public String index(){
        return "users/index";
    }

    @GetMapping("/orders")
    public String userOrders(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        model.addAttribute("user", usersDAO.findByUsername(username));

        return "users/orders";
    }

}
