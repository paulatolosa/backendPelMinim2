package manager;

import database.models.Usuario;

public interface AuthManager {
    void register(String username, String password);
    Usuario login(String username, String password);
}
