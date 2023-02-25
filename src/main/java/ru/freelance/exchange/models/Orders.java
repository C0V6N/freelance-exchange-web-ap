package ru.freelance.exchange.models;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class Orders {
    // ID заказа
    private int id;
    // ...

    // Заголовок заказа
    @Size(min = 5,
            message = "Название вакансии должно содержать минимум 5 символов.")
    private String title;

    // Описание заказа
    @Size(min = 5,
            message = "Описание вакансии должно содержать минимум 5 символов.")
    private String description;

    // Сумма денежного вознаграждения
    @Min(value = 500,
            message = "Стоимость заказа не должна быть меньше 500 рублей.")
    private float salary;

    // ID категории деятельности и её название
    private int category;
    private String categoryTitle;

    // ID услуги и её название
    private int service;
    private String serviceTitle;

    // Адрес хранения вложений
    private String attachments;

    // Деадлайн заказа
    private String deadline;

    // Дата создания вакансии
    private String orderDate;

    // ID заказчика
    private int employer;

    // ID исполнителя заказа
    private int freelancer;

    //1 - на модерации, 2 - активный, 3 - на выполнении,
    // 4 - завершенный, 5 - отменённый
    private byte status;

    // Отзыв на выполненную работу
    private String review;

    // Оценка выполненной работы
    private byte rating;


    private int responses;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getEmployer() {
        return employer;
    }

    public void setEmployer(int employer) {
        this.employer = employer;
    }

    public int getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(int freelancer) {
        this.freelancer = freelancer;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public byte getRating() {
        return rating;
    }

    public void setRating(byte rating) {
        this.rating = rating;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public int getResponses() {
        return responses;
    }

    public void setResponses(int responses) {
        this.responses = responses;
    }
}
