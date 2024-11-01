package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
import java.util.List;
import java.util.Map;
import java.util.Optional;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;

public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;

    // Default constructor for testing
    public SocialMediaController() {
        AccountDAO accountDAO = new AccountDAO();
        MessageDAO messageDAO = new MessageDAO();
        
        this.accountService = new AccountService(accountDAO);
        this.messageService = new MessageService(messageDAO, accountDAO);
    }

    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // Set Content-Type to application/json for all responses
        app.before(ctx -> ctx.header("Content-Type", "application/json"));

        // Define endpoints with updated parameter syntax
        app.post("/register", this::registerAccount);
        app.post("/login", this::loginAccount);
        app.post("/messages", this::createMessage);
        app.get("/messages", this::getAllMessages);
        app.get("/messages/{id}", this::getMessageById);  // Updated to {id}
        app.delete("/messages/{id}", this::deleteMessageById);  // Updated to {id}
        app.patch("/messages/{id}", this::updateMessageText);  // Updated to {id}
        app.get("/accounts/{account_id}/messages", this::getMessagesByUserId);  // Updated to {account_id}

        return app;
    }

    // Endpoint: POST /register
    private void registerAccount(Context ctx) {
        Account account = ctx.bodyAsClass(Account.class);
        Optional<Account> registeredAccount = accountService.registerAccount(account);

        if (registeredAccount.isPresent()) {
            ctx.json(registeredAccount.get()).status(200);
        } else {
            ctx.status(400).result("");
        }
    }

    // Endpoint: POST /login
    private void loginAccount(Context ctx) {
        Account loginRequest = ctx.bodyAsClass(Account.class);
        Optional<Account> loggedInAccount = accountService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (loggedInAccount.isPresent()) {
            ctx.json(loggedInAccount.get()).status(200);
        } else {
            ctx.status(401).result("");
        }
    }

    // Endpoint: POST /messages
    private void createMessage(Context ctx) {
        Message message = ctx.bodyAsClass(Message.class);
        Optional<Message> createdMessage = messageService.createMessage(message);
    
        if (createdMessage.isPresent()) {
            ctx.json(createdMessage.get()).status(200); // Automatically returns JSON
        } else {
            ctx.status(400).result("");
        }
    }

    

    // Endpoint: GET /messages
    private void getAllMessages(Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages).status(200);
    }

    // Endpoint: GET /messages/:id
    private void getMessageById(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("id"));
        Optional<Message> message = messageService.getMessageById(messageId);

        if (message.isPresent()) {
            ctx.json(message.get()).status(200);
        } else {
            ctx.status(200).result("");
        }
    }

    // Endpoint: DELETE /messages/:id
    private void deleteMessageById(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("id"));
        Optional<Message> deletedMessage = messageService.deleteMessageById(messageId);

        if (deletedMessage.isPresent()) {
            ctx.json(deletedMessage.get()).status(200);
        } else {
            ctx.status(200).result("");
        }
    }

    // Endpoint: PATCH /messages/:id
    private void updateMessageText(Context ctx) {
    int messageId = Integer.parseInt(ctx.pathParam("id"));
    String newText = ctx.bodyAsClass(Map.class).get("message_text").toString(); // Extract new message text

    Optional<Message> updatedMessage = messageService.updateMessageText(messageId, newText);

    if (updatedMessage.isPresent()) {
        ctx.json(updatedMessage.get()).status(200); // Return updated message if successful
    } else {
        ctx.status(400).result("");
    }
}


    // Endpoint: GET /accounts/:account_id/messages
    private void getMessagesByUserId(Context ctx) {
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> userMessages = messageService.getMessagesByUserId(accountId);

        ctx.json(userMessages).status(200);
    }
}
