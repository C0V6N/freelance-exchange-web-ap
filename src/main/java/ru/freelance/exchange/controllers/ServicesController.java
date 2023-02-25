package ru.freelance.exchange.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.freelance.exchange.dao.ServicesDAO;

import java.util.List;

@RestController
public class ServicesController {
    private final ServicesDAO servicesDAO;

    @Autowired
    public ServicesController(ServicesDAO servicesDAO) {
        this.servicesDAO = servicesDAO;
    }

    @GetMapping("/services")
    public List<List<String>> getServices(@RequestParam int categoryId) {
        return servicesDAO.index(categoryId);
    }
}
