package ru.freelance.exchange.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;
import ru.freelance.exchange.models.Orders;
import ru.freelance.exchange.models.Services;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ServicesDAO {
    @Autowired
    private DataSource dataSource;

    public List<List<String>> index(int categoryId) {

        List<List<String>> servicesList = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "SELECT * FROM services WHERE category_id=?"
             )) {

            preparedStatement.setInt(1, categoryId);

            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                List<String> service = new ArrayList<>();

                service.add(Integer.toString(result.getInt("ID")));
                service.add(result.getString("service_title"));
                service.add(Integer.toString(result.getInt("category_id")));

                servicesList.add(service);
            }

            return servicesList;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public String findTitleById (int id) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "SELECT service_title FROM services where ID=?"
             ))  {

            preparedStatement.setInt(1, id);

            ResultSet result = preparedStatement.executeQuery();

            result.next();

            return result.getString("service_title");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }
}
