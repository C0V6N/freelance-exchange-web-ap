package ru.freelance.exchange.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class TransactionsDAO {
    @Autowired
    private DataSource dataSource;

    public void addTransaction(int userId, float amount) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "INSERT INTO transactions VALUES(?,?,?)"
             )) {

            preparedStatement.setInt(1,userId);
            preparedStatement.setFloat(2, amount);

            SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            String dateNow = formatForDateNow.format(new Date());

            preparedStatement.setString(3, dateNow);

            preparedStatement.executeUpdate();

            changeCash(userId, amount);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void changeCash(int userId, float amount) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "UPDATE users SET cash=cash+? WHERE ID=?"
             )) {

            preparedStatement.setFloat(1,amount);
            preparedStatement.setInt(2, userId);

            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
