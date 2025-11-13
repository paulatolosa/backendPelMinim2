package database.impl;

import database.BaseDeDatos;
import database.models.Item;
import database.models.Usuario;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDeDatosHashMap implements BaseDeDatos {
    private static final Logger LOGGER = Logger.getLogger(BaseDeDatosHashMap.class);
    private static BaseDeDatosHashMap instance;
    private Map<String, Usuario> usuarios;
    private Map<Integer, Item> items;

    private BaseDeDatosHashMap() {
        this.usuarios = new HashMap<>();
        this.items = new HashMap<>();

        // Añadir items de prueba para DEBUG
        Item item1 = new Item(1, "Espada legendaria", "Una espada muy poderosa", 100);
        Item item2 = new Item(2, "Escudo de diamante", "Un escudo indestructible", 150);
        this.items.put(item1.getId(), item1);
        this.items.put(item2.getId(), item2);

        LOGGER.info("Items de prueba añadidos a la base de datos, quitar en un futuro");
    }

    public static synchronized BaseDeDatosHashMap getInstance() {
        if (instance == null) {
            instance = new BaseDeDatosHashMap();
            LOGGER.info("Instancia de BaseDeDatosHashMap creada");
        }
        return instance;
    }

    @Override
    public void addUsuario(Usuario usuario) {
        LOGGER.info("Añadiendo usuario: " + usuario);
        usuarios.put(usuario.getUsername(), usuario);
    }

    @Override
    public Usuario getUsuario(String username) {
        return usuarios.get(username);
    }

    @Override
    public void removeUsuario(String username) {
        usuarios.remove(username);
    }

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item getItem(Integer id) {
        return items.get(id);
    }

    @Override
    public void addItem(Item item) {
        LOGGER.info("Añadiendo item: " + item);
        items.put(item.getId(), item);
    }

    @Override
    public void updateUsuario(Usuario usuario) {
        usuarios.put(usuario.getUsername(), usuario);
    }

    @Override
    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios.values());
    }
}
