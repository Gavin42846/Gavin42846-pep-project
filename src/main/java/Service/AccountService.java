package Service;

import java.util.Optional;
import Model.Account;
import DAO.AccountDAO;

public class AccountService {
    private final AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    // Method to handle registration
    public Optional<Account> registerAccount(Account account) {
        // Validation: username should not be blank, password should be at least 4 characters
        if (account.getUsername().isBlank() || account.getPassword().length() < 4) {
            return Optional.empty();
        }

        // Use DAO to check uniqueness and insert if valid
        return accountDAO.registerAccount(account);
    }

    // Method to handle login
    public Optional<Account> login(String username, String password) {
        // Validation: username and password should not be blank
        if (username.isBlank() || password.isBlank()) {
            return Optional.empty();
        }

        // Use DAO to authenticate
        return accountDAO.loginAccount(username, password);
    }
}
