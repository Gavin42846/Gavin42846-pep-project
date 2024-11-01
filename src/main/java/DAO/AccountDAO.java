package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Model.Account;
import Util.ConnectionUtil;
import java.sql.SQLException;

import java.util.Optional;


public class AccountDAO {
    public AccountDAO() {
        ensureDefaultUserExists();
    }

    // Ensures a default user with account_id = 1 exists
    private void ensureDefaultUserExists() {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String checkUserSql = "SELECT COUNT(*) FROM account WHERE account_id = 1";
            PreparedStatement checkStmt = connection.prepareStatement(checkUserSql);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) == 0) {
                // No user with account_id = 1, so insert default user
                String insertUserSql = "INSERT INTO account (account_id, username, password) VALUES (1, 'testuser1', 'password')";
                PreparedStatement insertStmt = connection.prepareStatement(insertUserSql);
                insertStmt.executeUpdate();
                insertStmt.close();
                
            }
            rs.close();
            checkStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean userExists(int userId) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT COUNT(*) FROM account WHERE account_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, userId);
    
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if user doesn't exist or if an error occurs
    }
    
    public Optional<Account> registerAccount(Account account)
    {
        Connection connection = null;
        PreparedStatement stmt = null;

        try{
            connection = ConnectionUtil.getConnection();
            //checking to see if user already is in db
            String checkSql = "SELECT * FROM account WHERE username = ?";
            stmt = connection.prepareStatement(checkSql);
            stmt.setString(1, account.getUsername());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return Optional.empty(); //user already exists
            }
            rs.close();
            stmt.close();

            //making new account if user didnt exist
            String insertSql = "INSERT INTO account (username, password) VALUES (?, ?)";
            stmt = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getPassword());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if(generatedKeys.next()){
                int accountId = generatedKeys.getInt(1);
                account.setAccount_id(accountId);
                return Optional.of(account); //registration successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if( stmt != null) try { stmt.close();} catch (SQLException e) {e.printStackTrace();}
        }
        return Optional.empty(); //registration failed
    }

    //Method used for logging in
    public Optional<Account> loginAccount(String username, String password){
        Connection connection = null;
        PreparedStatement stmt = null;

        try{
            connection = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1,username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                Account account = new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
                return Optional.of(account); // login successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(stmt != null) try {stmt.close();} catch (SQLException e) {e.printStackTrace();}
        }
        return Optional.empty(); //login failed
    }
    
}
