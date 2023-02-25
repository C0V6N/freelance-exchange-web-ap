package ru.freelance.exchange.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.freelance.exchange.models.Orders;
import ru.freelance.exchange.models.Responses;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ResponsesDAO {
    @Autowired
    private DataSource dataSource;

    public List<Responses> findByOrder(int orderId) {
        List<Responses> responses = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "SELECT r.*, u.first_name, u.last_name, u.profile_picture FROM responses r " +
                             "INNER JOIN users u ON u.ID=r.id_freelancer " +
                             "WHERE r.id_order=?"
             )) {

            preparedStatement.setInt(1, orderId);

            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                Responses response = new Responses();

                response.setId(result.getInt("ID"));
                response.setOrderId(result.getInt("id_order"));
                response.setUserId(result.getInt("id_freelancer"));

                String orderDateFormat = result.getString("response_date");
                Date formatOrderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(orderDateFormat.substring(0, orderDateFormat.length()-2));
                SimpleDateFormat orderDate = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                response.setResponseDate(orderDate.format(formatOrderDate));

                response.setUserName(result.getString("first_name") + " " + result.getString("last_name"));
                response.setUserPhoto(result.getString("profile_picture"));

                responses.add(response);
            }

            return responses;

        } catch (SQLException | ParseException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public int countResponses(int orderId) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "SELECT COUNT(*) as responsesCount FROM responses WHERE id_order=?"
             )) {

            preparedStatement.setInt(1, orderId);

            ResultSet result = preparedStatement.executeQuery();

            result.next();

            return result.getInt("responsesCount");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    public void addResponse(int userId, int orderId) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "INSERT INTO responses VALUES (?,?,?)"
             )) {

            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, userId);

            SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            String dateNow = formatForDateNow.format(new Date());

            preparedStatement.setString(3, dateNow);

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean isResponseLeaved(int userId, int orderId) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "SELECT * FROM responses WHERE id_order=? AND id_freelancer=?"
             )) {

            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, userId);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next())
                return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}
