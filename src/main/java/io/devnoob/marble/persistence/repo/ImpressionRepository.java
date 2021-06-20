package io.devnoob.marble.persistence.repo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import io.devnoob.marble.persistence.entity.Impression;

@Service
public class ImpressionRepository extends DataRepository<Impression, Long> {

    @Override
    public void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS impression (" + 
                        "id         integer PRIMARY KEY," + 
                        "path       text NOT NULL," + 
                        "marble_id  integer NOT NULL," +
                        "type       integer NOT Null" + 
                 ");";
        Statement statement = connection.createStatement();
        statement.execute(query); 
    }

    @Override
    public Impression find(Long id) {
        Impression impression = null;
        try {
            String query = "SELECT * FROM impression WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            while(result.next()) {
                impression = new Impression(
                    result.getLong("id"),
                    result.getString("path"), 
                    result.getLong("marble_id"),
                    result.getInt("type")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return impression;
    }

    @Override
    public List<Impression> findAll() {
        List<Impression> impressions = new LinkedList<>();
        try {
            String query = "SELECT * FROM impression;";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);

            while(result.next()) {
                impressions.add(new Impression(
                    result.getLong("id"),
                    result.getString("path"),
                    result.getLong("marble_id"),
                    result.getInt("type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return impressions;
    }

    @Override
    public boolean insert(Impression obj) {
        boolean isSuccess = false;
        try {
            String query = "INSERT INTO impression(path, marble_id, type) VALUES(?,?,?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, obj.getPath());
            statement.setLong(2, obj.getMarbleId());
            statement.setLong(3, obj.getType());

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
            String query = "DELETE FROM impression WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            isSuccess = statement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    @Override
    public boolean update(Impression obj) {
        boolean isSuccess = false;
        try {
            String query = "UPDATE impression SET path=?, marble_id=?, type=? WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, obj.getPath());
            statement.setLong(2, obj.getMarbleId());
            statement.setInt(3, obj.getType());
            statement.setLong(4, obj.getId());
            isSuccess = statement.executeUpdate() == 1;
        } catch (SQLException error) {
            error.printStackTrace();
        }
        return isSuccess;
    }

    public List<Impression> getImpressionsByMarbleId(Long id) {
        List<Impression> impressions = new LinkedList<>();
        try{
            String query = "SELECT * FROM impression WHERE marble_id=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                impressions.add(new Impression(
                    result.getLong("id"),
                    result.getString("path"),
                    result.getLong("marble_id"),
                    result.getInt("type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return impressions;
    }
}