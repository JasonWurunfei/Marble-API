package io.devnoob.marble.persistence.repo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import io.devnoob.marble.persistence.entity.User;

@Service
public class UserRepository extends DataRepository<User, Long> {

    @Override
    public void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS user (" +
                            "id         integer PRIMARY KEY," +
                            "username   text NOT NULL" +
                        ");";
        Statement statement = connection.createStatement();
        statement.execute(query);
    }
    
    @Override
    public User find(Long id) {
        User user = null;
        try {
            String query = "SELECT * FROM user WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            while(result.next()) {
                user = new User(
                    result.getLong("id"), 
                    result.getString("username")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    
    @Override
    public List<User> findAll() {
        List<User> users = new LinkedList<>();
        try {
            String query = "SELECT * FROM user;";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);

            while(result.next()) {
                users.add(new User(
                    result.getLong("id"), 
                    result.getString("username")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public boolean insert(User obj) {
        
        boolean isSuccess = false;
        try {
            String query = "INSERT INTO user(username) VALUES(?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, obj.getUsername());

            isSuccess = statement.executeUpdate() == 1;
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return isSuccess;
    }

    @Override
    public boolean delete(Long id) {
        boolean isSuccess = false;
        try {
            String query = "DELETE FROM user WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            isSuccess = statement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
    
}
