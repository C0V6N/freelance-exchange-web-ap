package ru.freelance.exchange.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.freelance.exchange.dao.*;
import ru.freelance.exchange.models.Orders;
import ru.freelance.exchange.models.Responses;
import ru.freelance.exchange.models.Users;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.DecimalFormat;

@Controller
@RequestMapping("/orders")
public class OrdersController {
    // Доступ к методам классов взаимодействия с таблицами БД
    private final UsersDAO usersDAO;
    private final OrdersDAO ordersDAO;
    private final CategoriesDAO categoriesDAO;
    private final ResponsesDAO responsesDAO;
    private final TransactionsDAO transactionsDAO;

    @Autowired
    public OrdersController(UsersDAO usersDAO, OrdersDAO ordersDAO, CategoriesDAO categoriesDAO, ResponsesDAO responsesDAO, TransactionsDAO transactionsDAO) {
        this.usersDAO = usersDAO;
        this.ordersDAO = ordersDAO;
        this.categoriesDAO = categoriesDAO;
        this.responsesDAO = responsesDAO;
        this.transactionsDAO = transactionsDAO;
    }

    // Обработчик HTTP GET-запроса на странице вакансий
    @GetMapping()
    public String index(Model model) {
        // Получение имени пользователя из текущей сессии
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Получение уровня доступа пользователя
        byte userAccess = usersDAO.findByUsername(username).getAccess();

        // Добавление атрибутов модели
        if (userAccess == 1) {
            model.addAttribute("orders", ordersDAO.index((byte) 1));
            model.addAttribute("countOrders", ordersDAO.getCountRows());
        } else {
            model.addAttribute("orders", ordersDAO.index((byte) 2));
            model.addAttribute("countOrders", ordersDAO.getCountRows());
        }

        model.addAttribute("user", usersDAO.findByUsername(username));

        model.addAttribute("userAccess", userAccess);

        DecimalFormat num = new DecimalFormat("###,###.##");
        model.addAttribute("floatNum", num);

        // Возвращение адреса представления страницы
        return "orders/index";
    }


    @GetMapping("/new_order")
    public String newOrder(Model model, @ModelAttribute("order") Orders order) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        model.addAttribute("user", usersDAO.findByUsername(username));
        model.addAttribute("categories", categoriesDAO.index());

        return "orders/new_order";
    }

    @PostMapping("/new_order")
    public String addNewOrder(Model model, @ModelAttribute("order") @Valid Orders order, BindingResult bindingResult) {
        // Получение имени пользователя из текущей сессии
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Получение данных пользователя
        Users user = usersDAO.findByUsername(username);

        // Добавление атрибутов модели
        model.addAttribute("user", usersDAO.findByUsername(username));
        model.addAttribute("categories", categoriesDAO.index());

        // Является ли стоимость заказа дороже доступных пользователю средств
        if (order.getSalary() > user.getCash())
            model.addAttribute("cashError", true);

        if (bindingResult.hasErrors() || model.containsAttribute("cashError"))
            return "orders/new_order";

        // Добавление ID пользователя в заказ как заказчика
        order.setEmployer(user.getId());

        // Если заказ был создан, то происходит списание средств со счета заказчика
        if (ordersDAO.addOrder(order))
            transactionsDAO.addTransaction(order.getEmployer(), -order.getSalary());

        // Возвращение представления страницы с заказами
        return "redirect:/orders";
    }

    @GetMapping("/order-{id}")
    public String showOrder(Model model, @PathVariable("id") int id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("user", usersDAO.findByUsername(username));

        Users user = usersDAO.findByUsername(username);

        Orders order = ordersDAO.findById(id);

        model.addAttribute("order", order);

        model.addAttribute("employer", usersDAO.findById(order.getEmployer()));

        DecimalFormat num = new DecimalFormat("###,###.##");
        model.addAttribute("floatNum", num);

        model.addAttribute("responses", responsesDAO.findByOrder(id));

        model.addAttribute("responsesCount", responsesDAO.countResponses(id));

        model.addAttribute("isAuthor", ordersDAO.isAuthor(id, usersDAO.findIdByUsername(username)));

        model.addAttribute("isResponseLeaved", responsesDAO.isResponseLeaved(user.getId(), order.getId()));

        model.addAttribute("userAccess", user.getAccess());

        if (order.getFreelancer() != 0)
            model.addAttribute("freelancer", usersDAO.findById(order.getFreelancer()));

        return "/orders/order";
    }

    @GetMapping("/category-{id}")
    public String showCategoryOrders() {
        return "";
    }

    @PostMapping("/response-{id}")
    public String addResponse(@PathVariable("id") int orderId, HttpServletRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        int userId = usersDAO.findIdByUsername(username);

        responsesDAO.addResponse(userId, orderId);

        String referer = request.getHeader("Referer");

        return "redirect:"+ referer;
    }

    @PostMapping("/accept-order-{id}")
    public String acceptOrder(@PathVariable("id") int orderId) {
        ordersDAO.changeOrderStatus(orderId, (byte) 2);
        return "redirect:/orders";
    }

    @GetMapping("/order-{id}/edit")
    public String showEditOrder(@PathVariable("id") int orderId, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        model.addAttribute("order", ordersDAO.findById(orderId));
        model.addAttribute("user", usersDAO.findByUsername(username));
        model.addAttribute("categories", categoriesDAO.index());

        return "orders/edit";
    }

    @PatchMapping("/order-{id}/edit")
    public String editOrder(@PathVariable("id") int orderId, Model model, @ModelAttribute("order") @Valid Orders order) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        model.addAttribute("order", ordersDAO.findById(orderId));
        model.addAttribute("user", usersDAO.findByUsername(username));
        model.addAttribute("categories", categoriesDAO.index());

        ordersDAO.editOrder(order);

        return "redirect:/orders/order-" + order.getId();
    }

    @PostMapping("/choose-{orderId}-freelancer-{userId}")
    public String chooseFreelancer(@PathVariable("orderId") int orderId, @PathVariable("userId") int userId, HttpServletRequest request) {

        ordersDAO.chooseFreelancer(orderId, userId);

        String referer = request.getHeader("Referer");

        return "redirect:"+ referer;
    }

    @PostMapping("/complete-order-{id}")
    public String completeOrder(@PathVariable("id") int orderId, HttpServletRequest request) {
        // Получение имени пользователя из текущей сессии
        Orders order = ordersDAO.findById(orderId);

        // Изменение статуса заказа на завершённый
        ordersDAO.changeOrderStatus(orderId, (byte) 4);

        // Перевод замороженных средств на счёт исполнителя заказа
        transactionsDAO.changeCash(order.getFreelancer(), order.getSalary());

        // Перенаправление на страницу, с которой была отправлена форма
        String referer = request.getHeader("Referer");

        return "redirect:"+ referer;
    }

    @PostMapping("/cancel-order-{id}")
    public String cancelOrder(@PathVariable("id") int orderId, HttpServletRequest request) {
        Orders order = ordersDAO.findById(orderId);

        ordersDAO.changeOrderStatus(orderId, (byte) 5);

        transactionsDAO.changeCash(order.getEmployer(), order.getSalary());

        String referer = request.getHeader("Referer");

        return "redirect:"+ referer;
    }
}
