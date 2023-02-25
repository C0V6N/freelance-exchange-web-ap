package ru.freelance.exchange.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.freelance.exchange.dao.OrdersDAO;
import ru.freelance.exchange.dao.ServicesDAO;
import ru.freelance.exchange.models.Orders;

import java.util.List;

@RestController
public class SearchController {
    private final OrdersDAO ordersDAO;

    @Autowired
    public SearchController(OrdersDAO ordersDAO) {
        this.ordersDAO = ordersDAO;
    }

    @GetMapping("/search")
    public List<List<String>> search(@RequestParam("term") String query) {
        return ordersDAO.searchOrders(query);
    }
}
