package DAO;

import java.sql.*;

import Util.ConnectionUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import Model.Message;

public class MessageDAO {

    // Method to create a new message
    /*public Optional<Message> createMessage(Message message) {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = ConnectionUtil.getConnection();

            // Validate that the 'posted_by' account exists
            String checkSql = "SELECT * FROM account WHERE account_id = ?";
            stmt = connection.prepareStatement(checkSql);
            stmt.setInt(1, message.getPosted_by());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) return Optional.empty(); // Account does not exist

            stmt.close();
            rs.close();

            // Insert new message
            String insertSql = "INSERT INTO message (message_text, posted_by, time_posted_epoch) VALUES (?, ?, ?)";
            stmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, message.getMessage_text());
            stmt.setInt(2, message.getPosted_by());
            stmt.setLong(3, message.getTime_posted_epoch());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int messageId = generatedKeys.getInt(1);
                message.setMessage_id(messageId); // Set the generated message_id in the Message object
                return Optional.of(message); // Message creation successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return Optional.empty(); // Message creation failed
    }*/

    public Optional<Message> createMessage(Message message) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "INSERT INTO message (message_text, posted_by, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, message.getMessage_text());
            stmt.setInt(2, message.getPosted_by());
            stmt.setLong(3, message.getTime_posted_epoch());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    message.setMessage_id(generatedKeys.getInt(1));
                    System.out.println("Message created with ID: " + message.getMessage_id()); // Logging
                    return Optional.of(message);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean updateMessage(Message message) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, message.getMessage_text());
            stmt.setInt(2, message.getMessage_id());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if there was an issue with the update
    }
    
    // Method to retrieve all messages
    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM message";
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Message message = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                );
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return messages;
    }

    // Method to retrieve a message by ID
    public Optional<Message> getMessageById(int messageId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM message WHERE message_id = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, messageId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Message message = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                );
                return Optional.of(message); // Message found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return Optional.empty(); // Message not found
    }

    // Method to delete a message by ID
    public boolean deleteMessageById(int messageId) {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = ConnectionUtil.getConnection();
            String sql = "DELETE FROM message WHERE message_id = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, messageId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0; // True if the message was deleted
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false; // Deletion failed
    }

    // Method to update a message text by ID
    public Optional<Message> updateMessageText(int messageId, String newText) {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = ConnectionUtil.getConnection();
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, newText);
            stmt.setInt(2, messageId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return getMessageById(messageId); // Return the updated message
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return Optional.empty(); // Update failed
    }

    // Method to retrieve all messages by a specific user
    public List<Message> getMessagesByUserId(int userId) {
        List<Message> messages = new ArrayList<>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Message message = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                );
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return messages;
    }
}
