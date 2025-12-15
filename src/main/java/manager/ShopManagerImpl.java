package manager;


import db.orm.dao.*;
import db.orm.dao.IItemDAO;
import db.orm.dao.InventarioDAOImpl;
import db.orm.dao.InventarioDAO;
import db.orm.model.Inventario;
import db.orm.model.Item;
import db.orm.model.Usuario;

import org.apache.log4j.Logger;
import services.DTOs.ItemInventario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopManagerImpl implements ShopManager {
    private static final Logger LOGGER = Logger.getLogger(ShopManagerImpl.class);

    private static ShopManagerImpl instance;
    private final IItemDAO itemDAO;
    private final IUsuarioDAO usuarioDAO;
    private final InventarioDAO inventarioDAO;

    private ShopManagerImpl() {
        this.usuarioDAO = UsuarioDAOImpl.getInstance();
        this.itemDAO = ItemDAOImpl.getInstance();
        this.inventarioDAO = InventarioDAOImpl.getInstance();
    }

    public static ShopManagerImpl getInstance() {
        if (instance == null) {
            instance = new ShopManagerImpl();
            LOGGER.info("Instancia de ShopManagerImpl creada");
        }
        return instance;
    }


    // endpoint GET ITEMS TIENDA --> backend(services/ShopService.java): @GET, @Path("/items")
    //                           --> android(ShopService): @GET("/v1/shop/items")
    @Override
    public List<Item> getItemsTienda() {
        LOGGER.info("Obteniendo lista de items de la tienda");
        return itemDAO.getItems();
    }


    // endpoint BUY ITEM TENDA --> backend(services/ShopService.java): @POST, @Path("/buy/{itemId}")
    //                         --> android(ShopService): @POST("/v1/shop/buy/{id}")
    @Override
    public void comprarItem(String username, int itemId) {

        Usuario usuario = usuarioDAO.getUsuarioByUsername(username);
        if (usuario == null) {
            LOGGER.error("Intento de compra fallido: Usuario no encontrado: " + username);
            throw new RuntimeException("Usuario no encontrado");
        }

        Item item = itemDAO.getItem(itemId);
        if (item == null) {
            LOGGER.error("Intento de compra fallido: Item no encontrado: " + itemId);
            throw new RuntimeException("Item no encontrado");
        }

        int monedas = getMonedas(username);
        if (monedas < item.getPrecio()) throw new RuntimeException("Monedas insuficientes");;

        usuario.setMonedas(monedas-item.getPrecio());
        usuarioDAO.updateUsuario(usuario);

        LOGGER.info("Monedas de usuario: " + usuarioDAO.getUsuarioByUsername(username).getMonedas());

        Inventario inventario = new Inventario(usuario.getId(), item.getId());
        inventarioDAO.addInventario(inventario);

        LOGGER.info("Usuario '" + username + "' ha comprado el item: " + item);
    }


    // endpoint GET MONEDAS USUARIO --> backend(services/ShopService.java): @GET, @Path("/monedas/{username}")
    //                              --> android(ShopService): @GET("/v1/shop/monedas/{username}")

    @Override
    public int getMonedas(String username) {

        Usuario u = this.usuarioDAO.getUsuarioByUsername(username);

        if (u == null) {
            throw new RuntimeException("Usuari no trobat");
        }

        LOGGER.info("monedas: " + u.getMonedas());
        return u.getMonedas();
    }

//    @Override
//    public int getMonedas(String username) {
//
//        Usuario u = this.usuarioDAO.getUsuarioByUsername(username);
//        LOGGER.info("monedas:"+ u.getMonedas());
//
//        if (u == null) return -1;
//
//        return u.getMonedas();
//    }



    // endpoint GET RANKING USUARIOS --> backend(services/ShopService.java): @GET, @Path("/ranking")
    //                               --> android(ShopService): @GET("/v1/shop/ranking")
    @Override
    public List<Usuario> getRanking() {
        List<Usuario> ranking = usuarioDAO.getUsuariosRanking();
        return ranking;
    }


    // endpoint GET PERFIL USUARIO --> backend(services/ShopService.java): @GET, @Path("/perfil/{username}")
    //                             --> android(ShopService): @GET("/v1/shop/perfil/{username}")
    @Override
    public Usuario getPerfil(String username){

        Usuario u = this.usuarioDAO.getUsuarioByUsername(username);

        if(u == null) {
            throw new RuntimeException("Usuari no trobat");
        }

        LOGGER.info("Obtenint perfil de: " + u.getEmail());
        return u;
    }

//    @Override
//    public Usuario getPerfil(String username){
//        Usuario u = this.usuarioDAO.getUsuarioByUsername(username);
//        LOGGER.info("Obtenint perfil de: " + u.getEmail());
//        return u;
//    }



    public int getMejorPuntuacion(String username) {
        Usuario u = this.usuarioDAO.getUsuarioByUsername(username);
        if (u == null) return -1;
        return u.getMejorPuntuacion();
    }



    // Aquesta funció construeix l’inventari agregat d’un usuari agrupant les entrades per tipus d’item mitjançant un Map,
    // de manera que cada item apareix una sola vegada amb la seva quantitat total.
    public List<ItemInventario> getItemByUsuario(String username) {
        Usuario u = this.usuarioDAO.getUsuarioByUsername(username);

        if (u == null) {
            throw new RuntimeException("Usuari no trobat");
        }

        List<Inventario> inventarioList = this.inventarioDAO.getInventario(u.getId());

        // Usamos un Mapa para contar: Clave=ID del Item, Valor=Objeto ItemInventario
        // Serveix per agrupar els items iguals i comptar-ne la quantitat
        Map<Integer, ItemInventario> contador = new HashMap<>();

        if (inventarioList != null) {
            for (Inventario inv : inventarioList) {
                Item item = itemDAO.getItem(inv.getItemId());
                if (item != null) {
                    if (contador.containsKey(item.getId())) {
                        ItemInventario existente = contador.get(item.getId());
                        existente.setCantidad(existente.getCantidad() + 1);
                    } else {
                        contador.put(item.getId(), new ItemInventario(item, 1));
                    }
                }
            }
        }
        return new ArrayList<>(contador.values());  // Convertim els valors del mapa a una llista List<ItemInventario> i la retornem
        // retorna: Llista d'ItemInventario amb items agrupats i quantitats
        // [
        //  ItemInventario(Boost, cantidad=x),
        //  ItemInventario(Shield, cantidad=y),
        //  ...
        //]
    }


}

