package dataaccess;

import dataaccess.sql.SQLDAOFactory;
import model.dataaccess.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import dataaccess.DataAccessObject.*;

class SQLUserDAOTest {

    UserDAO userDAO;
    String username = "davhig22";
    String password = "pass123";
    String email = "davhig22@byu.edu";

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new SQLDAOFactory().getUserDAO();
        userDAO.clear();
    }
    @Test
    void getUserTest() throws DataAccessException {
        userDAO.createUser(username, password, email);

        UserData normal = userDAO.getUser(username);

        Assertions.assertNotNull(normal);
        Assertions.assertTrue(BCrypt.checkpw(password, normal.password()));
    }

    @Test
    void getUserFail() throws DataAccessException {
        Assertions.assertNull(userDAO.getUser(username));
        userDAO.createUser(username, password, email);
        Assertions.assertNull(userDAO.getUser("nonexistent"));
    }

    @Test
    void createUserTest() {
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(username, password, email));
        Assertions.assertDoesNotThrow(() -> userDAO.createUser("different_user", password, email));
    }

    @Test
    void createUserFail() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(null, password, null));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(null, null, null));
        userDAO.createUser(username, password, email);
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(username, password, email));
    }

    @Test
    void clear() throws DataAccessException {
        userDAO.createUser(username, password, email);

        Assertions.assertDoesNotThrow(() -> userDAO.clear());
        Assertions.assertDoesNotThrow(() -> userDAO.clear()); //Multiple clears
    }
}