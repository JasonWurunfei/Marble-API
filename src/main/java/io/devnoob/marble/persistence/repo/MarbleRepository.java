package io.devnoob.marble.persistence.repo;

import io.devnoob.marble.persistence.entity.Marble;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MarbleRepository extends DataRepository<Marble, Long> {

    @Override
    public void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS marble (" + 
                       "id         integer PRIMARY KEY," + 
                       "name       text NOT NULL," + 
                       "user_id    integer NOT NULL," + 
                       "creation_time   timestamp NOT NULL," +
                       "translation     text NOT NULL," +
                       "story      text NOT NULL" +   
                ");";
        Statement statement = connection.createStatement();
        statement.execute(query);
    }

    @Override
    public Marble find(Long id) {
        Marble marble = null;
        try {
            String query = "SELECT * FROM marble WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            while(result.next()) {
                marble = new Marble(
                    result.getLong("id"), 
                    result.getString("name"),
                    result.getLong("user_id"),
                    result.getTimestamp("creation_time"),
                    result.getString("translation"),
                    result.getString("story")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marble;
    }

    @Override
    public List<Marble> findAll() {
        List<Marble> marbles = new LinkedList<>();
        try {
            String query = "SELECT * FROM marble;";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);

            while(result.next()) {
                marbles.add(new Marble(
                    result.getLong("id"), 
                    result.getString("name"),
                    result.getLong("user_id"),
                    result.getTimestamp("creation_time"),
                    result.getString("translation"),
                    result.getString("story")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marbles;
    }

    @Override
    public boolean insert(Marble obj) {
        boolean isSuccess = false;
        try {
            String query = "INSERT INTO marble(name, user_id, creation_time, translation, story) VALUES(?,?,?,?,?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, obj.getName());
            statement.setLong(2, obj.getUserId());
            statement.setTimestamp(3, obj.getCreationTime());
            statement.setString(4, obj.getTranslation());
            statement.setString(5, obj.getStory());

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
            String query = "DELETE FROM marble WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            isSuccess = statement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    @Override
    public boolean update(Marble obj) {
        boolean isSuccess = false;
        try {
            String query = "UPDATE marble SET name=?, user_id=?, creation_time=?, translation=?, story=?  WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, obj.getName());
            statement.setLong(2, obj.getUserId());
            statement.setTimestamp(3, obj.getCreationTime());
            statement.setString(4, obj.getTranslation());
            statement.setString(5, obj.getStory());
            statement.setLong(6, obj.getId());
            isSuccess = statement.executeUpdate() == 1;
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return isSuccess;
    }

    public List<Marble> getMarblesByUserId(Long id) {
        List<Marble> marbles = new LinkedList<>();
        try {
            String query = "SELECT * FROM marble WHERE user_id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            while(result.next()) {
                marbles.add(new Marble(
                    result.getLong("id"), 
                    result.getString("name"),
                    result.getLong("user_id"),
                    result.getTimestamp("creation_time"),
                    result.getString("translation"),
                    result.getString("story")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marbles; 
    }
}
