package Service;

import Model.Message;
import Util.ConnectionUtil;
import DAO.MessageDAO;
import DAO.AccountDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MessageService {
    private final MessageDAO messageDAO;
    private final AccountDAO accountDAO;

    public MessageService(MessageDAO messageDAO, AccountDAO accountDAO) {
        this.messageDAO = messageDAO;
        this.accountDAO = accountDAO;
    }

    // Method to create a message
    public Optional<Message> createMessage(Message message) {
        // Validation: message text must not be blank or exceed 255 characters
        if (message.getMessage_text() == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
            return Optional.empty();
        }

        // Verify that the user exists in the database
        if (!accountDAO.userExists(message.getPosted_by())) { // Implement userExists in AccountDAO
        return Optional.empty();
        }

        // Use DAO to insert the message if valid
        return messageDAO.createMessage(message);
    }

    // Method to retrieve all messages
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    // Method to retrieve a message by ID
    public Optional<Message> getMessageById(int messageId) {
        return messageDAO.getMessageById(messageId);
    }

    // Method to delete a message by ID
    public Optional<Message> deleteMessageById(int messageId) {
        Optional<Message> messageToDelete = messageDAO.getMessageById(messageId);
        if (messageToDelete.isPresent()) {
            boolean deleted = messageDAO.deleteMessageById(messageId);
            if (deleted) {
                return messageToDelete; // Return the deleted message
            }
        }
        return Optional.empty(); // Message not found or deletion failed
    }

    public Optional<Message> updateMessageText(int messageId, String newText) {
        // Check if new message text is valid
        if (newText == null || newText.isBlank() || newText.length() > 255) {
            return Optional.empty();
        }
    
        // Check if the message exists
        Optional<Message> existingMessage = messageDAO.getMessageById(messageId);
        if (existingMessage.isPresent()) {
            // Update the message text
            Message updatedMessage = existingMessage.get();
            updatedMessage.setMessage_text(newText);
            boolean updated = messageDAO.updateMessage(updatedMessage); // Now defined in MessageDAO
            if (updated) {
                return Optional.of(updatedMessage); // Return updated message if successful
            }
        }
        return Optional.empty(); // Return empty if update failed or message not found
    }
    

    // Method to retrieve all messages by a specific user
    public List<Message> getMessagesByUserId(int userId) {
        return messageDAO.getMessagesByUserId(userId);
    }
}

