package io.devnoob.marble.persistence.repo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Wu Runfei
 */
public abstract class DataRepository<T, ID> {

    private String dbPath = "database.db"; 
    
    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    protected Connection connection;

    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will create the corresponding table 
     * in the database if the table does not exist.
     * @throws SQLException
     */
    abstract public void createTable() throws SQLException;

    /**
     * This method will use the given primary key to look up the 
     * database to find the matching data record and return the 
     * object model.
     * @param id primary key
     * @return record object
     */
    abstract public T find(ID id);

    /**
     * This method will get all the corresponding data records 
     * and return the object model list
     * @return record object List
     */
    abstract public List<T> findAll();
    
    /**
     * This method will create a new record in the
     * database according to the given object model.
     * @param obj object model
     * @return true if the insertion is success otherwise false.
     */
    abstract public boolean insert(T obj);

    /**
     * This method will update the record according 
     * to the given object model.
     * @param obj
     * @return true if the update is success otherwise false.
     */
    abstract public boolean update(T obj);

    /**
     * This method will use the given ID to
     * remove a data record in the database
     * @param id
     * @return true if the deletion is success otherwise false.
     */
    abstract public boolean delete(ID id);
}
