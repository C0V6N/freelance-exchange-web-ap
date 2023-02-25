package ru.freelance.exchange.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.freelance.exchange.models.Categories;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CategoriesDAO {
    @Autowired
    private DataSource dataSource;

    public List<Categories> index() {
        List<Categories> categories = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "SELECT * FROM categories"
             ))  {

            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                Categories category = new Categories();
                category.setId(result.getInt("ID"));
                category.setTitle(result.getString("category_title"));
                categories.add(category);
            }

            return categories;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public String findTitleById(int id) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "SELECT category_title FROM categories where ID=?"
             ))  {

            preparedStatement.setInt(1, id);

            ResultSet result = preparedStatement.executeQuery();

            result.next();

            return result.getString("category_title");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }
}
