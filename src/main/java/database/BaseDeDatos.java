package database;

import database.models.Usuario;

public interface BaseDeDatos {
    void addUsuario(Usuario usuario);
    Usuario getUsuario(String username);
    void removeUsuario(String username);
    void updateUsuario(Usuario usuario);
}
