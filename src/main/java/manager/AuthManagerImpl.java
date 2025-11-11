package manager;

import database.BaseDeDatos;
import database.impl.BaseDeDatosHashMap;
import database.models.Usuario;

public class AuthManagerImpl implements AuthManager {

    private static AuthManagerImpl instance;
    private final BaseDeDatos baseDeDatos;

    private AuthManagerImpl() {
        this.baseDeDatos = BaseDeDatosHashMap.getInstance();
    }

    public static AuthManagerImpl getInstance() {
        if (instance == null) {
            instance = new AuthManagerImpl();
        }
        return instance;
    }

    @Override
    public void register(String username, String password) {
        if (baseDeDatos.getUsuario(username) != null) {
            throw new RuntimeException("El usuario ya existe");
        }
        baseDeDatos.addUsuario(new Usuario(username, password));
    }

    @Override
    public Usuario login(String username, String password) {
        Usuario usuario = baseDeDatos.getUsuario(username);
        if (usuario == null || !usuario.getPassword().equals(password)) {
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }
        return usuario;
    }
}
