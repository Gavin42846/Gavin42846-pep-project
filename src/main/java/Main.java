import Controller.SocialMediaController;
import io.javalin.Javalin;

import DAO.AccountDAO;
import DAO.MessageDAO;

import Service.AccountService;
import Service.MessageService;

/**
 * This class is provided with a main method to allow you to manually run and test your application. This class will not
 * affect your program in any way and you may write whatever code you like here.
 */
/*public class Main {
    public static void main(String[] args) {
        SocialMediaController controller = new SocialMediaController();
        Javalin app = controller.startAPI();
        app.start(8080);
    }
}*/
public class Main {
    public static void main(String[] args) {
        // Instantiate DAO objects
        AccountDAO accountDAO = new AccountDAO();
        MessageDAO messageDAO = new MessageDAO();

        // Instantiate Service objects with DAOs
        AccountService accountService = new AccountService(accountDAO);
        MessageService messageService = new MessageService(messageDAO, accountDAO);

        // Instantiate the controller with services
        SocialMediaController controller = new SocialMediaController(accountService, messageService);

        // Start the Javalin app
        Javalin app = controller.startAPI();
        app.start(8080);
    }
}

