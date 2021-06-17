package io.devnoob.marble.persistence.repo;

import io.devnoob.marble.persistence.entity.Bag;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BagRepository extends DataRepository<Bag, Long> {

    @Override
    public void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS bag (" + 
                       "id         integer PRIMARY KEY," + 
                       "user_id    integer NOT NULL," + 
                       "name       text NOT NULL," + 
                       "creation_time   timestamp NOT NULL" + 
                ");";
        Statement statement = connection.createStatement();
        statement.execute(query);
    }

    @Override
    public Bag find(Long id) {
        Bag bag = null;
        try {
            String query = "SELECT * FROM bag WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            while(result.next()) {
                bag = new Bag(
                    result.getLong("id"), 
                    result.getLong("user_id"),
                    result.getString("name"),
                    result.getTimestamp("creation_time")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bag;
    }

    @Override
    public List<Bag> findAll() {
        List<Bag> bags = new LinkedList<>();
        try {
            String query = "SELECT * FROM bag;";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);

            while(result.next()) {
                bags.add(new Bag(
                    result.getLong("id"), 
                    result.getLong("user_id"),
                    result.getString("name"),
                    result.getTimestamp("creation_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bags;
    }

    @Override
    public boolean insert(Bag obj) {
        boolean isSuccess = false;
        try {
            String query = "INSERT INTO bag(user_id, name, creation_time) VALUES(?,?,?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, obj.getuserId());
            statement.setString(2, obj.getName());
            statement.setTimestamp(3, obj.getcreationTime());

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
            String query = "DELETE FROM bag WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            isSuccess = statement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
