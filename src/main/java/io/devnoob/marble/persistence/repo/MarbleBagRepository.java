package io.devnoob.marble.persistence.repo;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import io.devnoob.marble.persistence.entity.Marble;
import io.devnoob.marble.persistence.entity.MarbleBag;

@Service
public class MarbleBagRepository extends DataRepository<MarbleBag, Long> {

    @Override
    public void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS marble_bag (" + 
                            "id         integer PRIMARY KEY," + 
                            "marble_id  integer NOT NULL," +
                            "bag_id     integer NOT NULL" +
                        ");";
        Statement statement = connection.createStatement();
        statement.execute(query); 
    }

    @Override
    public MarbleBag find(Long id) {
        MarbleBag marvbBag = null;
        try {
            String query = "SELECT * FROM marble_bag WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            while(result.next()) {
                marvbBag = new MarbleBag(
                    result.getLong("id"),
                    result.getLong("marble_id"),
                    result.getLong("bag_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marvbBag;
    }

    @Override
    public List<MarbleBag> findAll() {
        List<MarbleBag> marvbBags = new LinkedList<>();
        try {
            String query = "SELECT * FROM marble_bag;";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);

            while(result.next()) {
                marvbBags.add(new MarbleBag(
                    result.getLong("id"),
                    result.getLong("marble_id"),
                    result.getLong("bag_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marvbBags;
    }

    @Override
    public boolean insert(MarbleBag obj) {
        boolean isSuccess = false;
        try {
            String query = "INSERT INTO marble_bag(marble_id, bag_id) VALUES(?,?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, obj.getMarbleId());
            statement.setLong(2, obj.getBagId());

            isSuccess = statement.executeUpdate() == 1;
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return isSuccess;
    }

    @Override
    public boolean update(MarbleBag obj) {
        boolean isSuccess = false;
        try {
            String query = "UPDATE marble_bag SET marble_id=?, bag_id=? WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, obj.getMarbleId());
            statement.setLong(2, obj.getBagId());
            statement.setLong(3, obj.getId());
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
            String query = "DELETE FROM marble_bag WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            isSuccess = statement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public List<Marble> getMarbleByBagId(Long bagId) {
        List<Marble> marbles = new LinkedList<>();
        try {
            String query = "SELECT * FROM marble WHERE id IN " +
                           "(SELECT marble_id FROM " +
                           "bag INNER JOIN marble_bag ON bag.id=marble_bag.bag_id " +
                           "WHERE bag.id=?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, bagId);
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
