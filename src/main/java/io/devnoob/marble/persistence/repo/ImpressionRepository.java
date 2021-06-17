package io.devnoob.marble.persistence.repo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import io.devnoob.marble.persistence.entity.Impression;

public class ImpressionRepository extends DataRepository<Impression, String> {

    @Override
    public void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS impression (" + 
                        "path       text PRIMARY KEY," + 
                        "marble_id  integer NOT NULL," +
                        "type       integer NOT Null" + 
                 ");";
        Statement statement = connection.createStatement();
        statement.execute(query); 
    }

    @Override
    public Impression find(String path) {
        Impression impression = null;
        try {
            String query = "SELECT * FROM impression WHERE path=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, path);
            ResultSet result = statement.executeQuery();
            while(result.next()) {
                impression = new Impression(
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
    public boolean delete(String path) {
        boolean isSuccess = false;
        try {
            String query = "DELETE FROM impression WHERE path=?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, path);
            isSuccess = statement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}