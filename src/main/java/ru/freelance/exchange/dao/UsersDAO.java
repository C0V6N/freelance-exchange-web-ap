package ru.freelance.exchange.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.freelance.exchange.models.Users;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class UsersDAO {
    @Autowired
    private DataSource dataSource;

    public Users findByUsername(String email) {
        Users user = new Users();

        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "SELECT u.*, r.title FROM users u INNER JOIN roles r ON u.access=r.ID WHERE u.email=?"
             )) {

            preparedStatement.setString(1, email);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                user.setId(result.getInt("ID"));
                user.setFirstName(result.getString("first_name"));
                user.setSecondName(result.getString("last_name"));
                user.setCash(result.getFloat("cash"));
                user.setEmail((result.getString("email")));
                user.setPassword(result.getString("password"));
                user.setProfilePicture(result.getString("profile_picture"));
                user.setRole(result.getString("title"));
                user.setAccess(result.getByte("access"));
                user.setStatus(result.getBoolean("status"));

                return user;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

        return null;
    }

    //Добавление нового пользователя
    public void new_user(Users user) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "INSERT INTO users VALUES(?,?,?,?,?,?,?,?)"
             )) {

            //Хеширование пароля
            BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
            String password = pwdEncoder.encode(user.getPassword());

            //Вставка значений
            preparedStatement.setString(1, "Пользователь");
            preparedStatement.setString(2, "0");
            preparedStatement.setFloat(3, 0);
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, password);
            preparedStatement.setString(6, "default_photo.png");
            preparedStatement.setByte(7, user.getAccess());
            preparedStatement.setBoolean(8, true);

            //Выполнение запроса
            preparedStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //Проверка уникальности Email-адреса
    public boolean isEmailUnique(String email){
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "SELECT COUNT(*) AS 'row_count' FROM users WHERE email=?"
             )) {

            preparedStatement.setString(1, email);

            ResultSet result = preparedStatement.executeQuery();
            result.next();

            int countRows = result.getInt("row_count");

            //Если число строк = 0 - вернёт истину, иначе - ложь
            return countRows == 0;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public int findIdByUsername(String username) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "SELECT ID FROM users WHERE email=?"
             )) {

            preparedStatement.setString(1, username);

            ResultSet result = preparedStatement.executeQuery();

            result.next();

            return result.getInt("ID");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    public Users findById(int id) {
        Users user = new Users();
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(
                     "SELECT u.*, r.title FROM users u INNER JOIN roles r ON u.access=r.ID WHERE u.ID=?"
             )) {

            preparedStatement.setInt(1, id);

            ResultSet result = preparedStatement.executeQuery();

            result.next();
            user.setId(result.getInt("ID"));
            user.setFirstName(result.getString("first_name"));
            user.setSecondName(result.getString("last_name"));
            user.setCash(result.getFloat("cash"));
            user.setEmail((result.getString("email")));
            user.setPassword(result.getString("password"));
            user.setProfilePicture(result.getString("profile_picture"));
            user.setRole(result.getString("title"));
            user.setStatus(result.getBoolean("status"));

            return user;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }
}
