package ru.freelance.exchange.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;
import ru.freelance.exchange.models.Orders;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class OrdersDAO {
    @Autowired
    private DataSource dataSource;

    private int countRows;

    public int getCountRows() {
        return countRows;
    }

    public void setCountRows(int countRows) {
        this.countRows = countRows;
    }

    // Метод получения списка заказов с необходимым статусом
    public List<Orders> index(byte ordersStatus) {

        List<Orders> ordersList = new ArrayList<>();

        countRows = 0;

        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "SELECT o.*, c.category_title, s.service_title FROM orders o " +
                             "INNER JOIN categories c ON o.category=c.ID " +
                             "INNER JOIN services s on o.service=s.ID " +
                             "WHERE o.status=? ORDER BY o.order_date DESC"
             );
             PreparedStatement statementResponses = con.prepareStatement(
                     "SELECT o.ID, COUNT(*) as responses FROM orders o " +
                             "INNER JOIN responses r ON r.id_order=o.ID " +
                             "WHERE o.ID=? GROUP BY o.ID"
             )) {

            preparedStatement.setByte(1, ordersStatus);

            ResultSet ordersResult = preparedStatement.executeQuery();

            while (ordersResult.next()) {
                Orders order = new Orders();

                order.setId(ordersResult.getInt("ID"));

                statementResponses.setInt(1, order.getId());
                ResultSet responsesResult = statementResponses.executeQuery();

                order.setTitle(ordersResult.getString("title"));
                order.setDescription(ordersResult.getString("description"));
                order.setSalary(ordersResult.getFloat("salary"));
                order.setCategory(ordersResult.getInt("category"));
                order.setService(ordersResult.getInt("service"));

                Date formatDate = new SimpleDateFormat("yyyy-MM-dd")
                        .parse(ordersResult.getString("deadline"));
                SimpleDateFormat deadline = new SimpleDateFormat("dd.MM.yyyy");

                order.setDeadline(deadline.format(formatDate));
                order.setCategoryTitle(ordersResult.getString("category_title"));
                order.setServiceTitle(ordersResult.getString("service_title"));

                if (!responsesResult.isBeforeFirst()) {
                    order.setResponses(0);
                } else {
                    responsesResult.next();
                    order.setResponses(responsesResult.getInt("responses"));
                }
                countRows++;
                ordersList.add(order);
            }
            return ordersList;

        } catch (SQLException | ParseException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public Orders findById(int orderId) {
        Orders order = new Orders();
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "SELECT o.*, c.category_title, s.service_title FROM orders o " +
                             "INNER JOIN categories c ON o.category=c.ID " +
                             "INNER JOIN services s on o.service=s.ID " +
                             "WHERE o.ID=? ORDER BY o.order_date DESC"
             );
             PreparedStatement statementResponses = con.prepareStatement(
                     "SELECT o.ID, COUNT(*) as responses FROM orders o " +
                             "INNER JOIN responses r ON r.id_order=o.ID " +
                             "WHERE o.ID=? GROUP BY o.ID"
             )) {

            preparedStatement.setInt(1, orderId);
            statementResponses.setInt(1, order.getId());

            ResultSet orderResult = preparedStatement.executeQuery();
            ResultSet responsesResult = statementResponses.executeQuery();

            orderResult.next();

            order.setId(orderResult.getInt("ID"));

            order.setTitle(orderResult.getString("title"));
            order.setDescription(orderResult.getString("description"));
            order.setSalary(orderResult.getFloat("salary"));
            order.setCategory(orderResult.getInt("category"));
            order.setService(orderResult.getInt("service"));

            Date formatDate = new SimpleDateFormat("yyyy-MM-dd").parse(orderResult.getString("deadline"));
            SimpleDateFormat deadline = new SimpleDateFormat("dd.MM.yyyy");
            order.setDeadline(deadline.format(formatDate));

            String orderDateFormat = orderResult.getString("order_date");
            Date formatOrderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(orderDateFormat.substring(0, orderDateFormat.length() - 2));
            SimpleDateFormat orderDate = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            order.setOrderDate(orderDate.format(formatOrderDate));

            order.setEmployer(orderResult.getInt("employer"));
            order.setFreelancer(orderResult.getInt("freelancer"));
            order.setCategoryTitle(orderResult.getString("category_title"));
            order.setServiceTitle(orderResult.getString("service_title"));
            order.setStatus(orderResult.getByte("status"));
            order.setReview(orderResult.getString("review"));
            order.setRating(orderResult.getByte("rating"));

            if (!responsesResult.isBeforeFirst()) {
                order.setResponses(0);
            } else {
                responsesResult.next();
                order.setResponses(responsesResult.getInt("responses"));
            }

            return order;

        } catch (SQLException | ParseException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public boolean addOrder(Orders order) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "INSERT INTO orders VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"
             )) {

            preparedStatement.setString(1, order.getTitle());
            preparedStatement.setString(2, order.getDescription());
            preparedStatement.setFloat(3, order.getSalary());
            preparedStatement.setInt(4, order.getCategory());
            preparedStatement.setInt(5, order.getService());
            preparedStatement.setString(6, order.getAttachments());
            preparedStatement.setString(7, order.getDeadline());

            SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            String dateNow = formatForDateNow.format(new Date());

            preparedStatement.setString(8, dateNow);
            preparedStatement.setInt(9, order.getEmployer());
            preparedStatement.setInt(10, order.getFreelancer());
            preparedStatement.setByte(11, (byte) 1);
            preparedStatement.setString(12, order.getReview());
            preparedStatement.setByte(13, order.getRating());

            preparedStatement.executeUpdate();

            return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public List<List<String>> searchOrders(String query) {
        List<List<String>> ordersList = new ArrayList<>();
        countRows = 0;
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "SELECT o.*, c.category_title, s.service_title FROM orders o " +
                             "INNER JOIN categories c ON o.category=c.ID " +
                             "INNER JOIN services s on o.service=s.ID " +
                             "WHERE o.status=2 AND LOWER(o.title) LIKE LOWER(CONCAT('%',?,'%')) " +
                             "ORDER BY o.order_date DESC"
             );
             PreparedStatement statementResponses = con.prepareStatement(
                     "SELECT o.ID, COUNT(*) as responses FROM orders o " +
                             "INNER JOIN responses r ON r.id_order=o.ID " +
                             "WHERE o.ID=? GROUP BY o.ID"
             )) {
            preparedStatement.setString(1, query);
            ResultSet ordersResult = preparedStatement.executeQuery();
            while (ordersResult.next()) {
                List<String> order = new ArrayList<>();
                order.add(Integer.toString(ordersResult.getInt("ID")));
                statementResponses.setInt(1, Integer.parseInt(order.get(0)));
                ResultSet responsesResult = statementResponses.executeQuery();
                order.add(ordersResult.getString("title"));
                order.add(ordersResult.getString("description"));
                DecimalFormat num = new DecimalFormat("###,###.##");
                order.add(num.format(ordersResult.getFloat("salary")));
                order.add(Integer.toString(ordersResult.getInt("category")));
                order.add(Integer.toString(ordersResult.getInt("service")));
                Date formatDate = new SimpleDateFormat("yyyy-MM-dd")
                        .parse(ordersResult.getString("deadline"));
                SimpleDateFormat deadline = new SimpleDateFormat("dd.MM.yyyy");
                order.add(deadline.format(formatDate));
                order.add(ordersResult.getString("category_title"));
                order.add(ordersResult.getString("service_title"));

                if (!responsesResult.isBeforeFirst()) {
                    order.add("0");
                } else {
                    responsesResult.next();
                    order.add(Integer.toString(responsesResult.getInt("responses")));
                }
                countRows++;
                ordersList.add(order);
            }

            return ordersList;

        } catch (SQLException | ParseException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public boolean isAuthor(int orderId, int userId) {

        Orders order = findById(orderId);

        return order.getEmployer() == userId;
    }

    public void changeOrderStatus(int orderId, byte newStatus) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "UPDATE orders SET status=? WHERE ID=?"
             )) {

            preparedStatement.setByte(1, newStatus);
            preparedStatement.setInt(2, orderId);

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void editOrder(Orders order) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "UPDATE orders SET title=?, description=?, salary=?, category=?, service=?, deadline=? WHERE id=?"
             )) {

            preparedStatement.setString(1, order.getTitle());
            preparedStatement.setString(2, order.getDescription());
            preparedStatement.setFloat(3, order.getSalary());
            preparedStatement.setInt(4, order.getCategory());
            preparedStatement.setInt(5, order.getService());
            preparedStatement.setString(6, order.getDeadline());

            preparedStatement.setInt(7, order.getId());

            preparedStatement.executeUpdate();

            changeOrderStatus(order.getId(), (byte) 1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void chooseFreelancer(int orderId, int freelancerId) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "UPDATE orders SET freelancer=? WHERE id=?"
             )) {

            preparedStatement.setInt(1, freelancerId);
            preparedStatement.setInt(2, orderId);

            preparedStatement.executeUpdate();

            changeOrderStatus(orderId, (byte) 3);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
