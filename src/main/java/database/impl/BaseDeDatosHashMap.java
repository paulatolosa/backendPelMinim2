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
        Item item1 = new Item(1, "Booster basic", "Un booster basico que te permite ir mas rapido durante unos segundos", 100);
        Item item2 = new Item(2, "Neumaticos avanzados", "Con estos neumaticos tienes mas agarre y velocidad augmentada", 150);
        this.items.put(item1.getId(), item1);
        this.items.put(item2.getId(), item2);
        Usuario admin = new Usuario("admin", "admin123", "admin", "admin", "admin@gmail.com", "10/10/10");
        Usuario arnau = new Usuario("arnau", "arnau123", "arnau", "munte", "arnau@gmail.com", "10/10/10");
        Usuario pablo = new Usuario("Pablito", "pablo123", "Pablo", "Nse", "pablo@gmail.com", "10/10/10");
        Usuario paula = new Usuario("Paula", "paula123", "Paula", "Nse", "paula@gmail.com", "10/10/10");
        usuarios.put("Paula", paula);
        usuarios.put("admin", admin);
        usuarios.put("arnau", arnau);
        usuarios.put("pablo", pablo);
        admin.setMejorPuntuacion(1000);
        arnau.setMejorPuntuacion(1200);
        pablo.setMejorPuntuacion(1500);
        paula.setMejorPuntuacion(1450);
        LOGGER.info("Items de prueba añadidos a la base de datos");
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

    public int getMonedas(String username) {
        Usuario usuario = usuarios.get(username);
        if (usuarios != null) return usuario.getMonedas();
        return -1;
    }

    public void setMonedas(String username, int monedas) {
        Usuario usuario = usuarios.get(username);
        if (usuario != null) usuario.setMonedas(monedas);
    }
}
