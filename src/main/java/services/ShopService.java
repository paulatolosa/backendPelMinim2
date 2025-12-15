package services;


import db.orm.model.Item;
import db.orm.model.Usuario;

import manager.ShopManagerImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.DTOs.CoinsResponse;
import services.DTOs.ItemInventario;
import services.DTOs.MessageResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Api(value = "/shop", description = "Servicios de la tienda de items")
@Path("/shop")
public class ShopService {

    private final ShopManagerImpl shopManager;

    public ShopService() {
        this.shopManager = ShopManagerImpl.getInstance();
    }


    // endpoint GET ITEMS TIENDA --> android(ShopService): @GET("/v1/shop/items")
    @GET
    @Path("/items")
    @ApiOperation(value = "Obtener todos los items de la tienda")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Item.class, responseContainer = "List") //
    })
    @Produces(MediaType.APPLICATION_JSON)

    public Response getItems() {
        List<Item> items = shopManager.getItemsTienda(); // manager/ShopManagerImpl.java, public List<Item> getItemsTienda()

        // GenericEntity s’utilitza a la resposta REST quan vols enviar una llista (o col·lecció) d’objectes
        GenericEntity<List<Item>> entity = new GenericEntity<List<Item>>(items) {};

        return Response.status(200) // 200: public List<Item> getItemsTienda() retorna la llista d'items
                .entity(entity)
                .build();
        // android: ShopActivity, loadItems(): onResponse() --> response.isSuccessful()
    }


    // endpoint BUY ITEM TENDA --> android(ShopService): @POST("/v1/shop/buy/{Id}")
    @POST
    @Path("/buy/{itemId}")
    @ApiOperation(value = "Comprar un item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Compra realitzada amb èxit", response = MessageResponse.class),
            @ApiResponse(code = 409, message = "Error en la compra de l'item", response = MessageResponse.class)
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Response buyItem(@PathParam("itemId") int itemId, String username) {

        if (username != null) username = username.replace("\"", "").trim();

        try {
            shopManager.comprarItem(username, itemId); // manager/ShopManagerImpl.java, public void comprarItem(String username, int itemId)

            return Response.status(200) // 200: public void comprarItem(String username, int itemId) no retorna res (compra realitzada amb èxit)
                    .entity(new MessageResponse("Compra realitzada amb èxit")) // serialitza missatge d'èxit i l'envia a android
                    .build();
            // android: ShopActivity, comprarItem(): onResponse() --> response.isSuccessful()

        } catch (RuntimeException e) {
            return Response.status(409) // 409: public void comprarItem(String username, int itemId) thow exception
                    .entity(new MessageResponse(e.getMessage())) // serialitza missatge d'error i l'envia a android
                    .build();
            // android: ShopActivity, comprarItem(): onResponse() --> !response.isSuccessful()
        }
    }


    // endpoint GET MONEDAS USUARIO --> android(ShopService): @GET("/v1/shop/monedas/{username}")
    @GET
    @Path("/monedas/{username}")
    @ApiOperation(value = "Obtenir monedes de l'usuari")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Monedes de l'usuari", response = CoinsResponse.class),
            @ApiResponse(code = 404, message = "Usuari no trobat", response = MessageResponse.class)
    })
    @Produces(MediaType.APPLICATION_JSON)

    public Response getCoins(@PathParam("username") String username) {
        try {
            int monedas = shopManager.getMonedas(username); // manager/ShopManagerImpl.java, public int getMonedas(String username)
            return Response.status(200) // 200: public int getMonedas(String username) retorna les monedes de l'usuari
                    .entity(new CoinsResponse(monedas))
                    .build();
            // android: ShopActivity, loadCoins(): onResponse() --> response.isSuccessful() && response.body() != null

        } catch (RuntimeException e) {
            return Response.status(404) // 404: public int getMonedas(String username) thow exception
                    .entity(new MessageResponse(e.getMessage()))
                    .build();
            // android: ShopActivity, loadCoins(): onResponse() --> !response.isSuccessful()
        }

    }


    // endpoint GET RANKING USUARIOS --> android(ShopService): @GET("/v1/shop/ranking")
    @GET
    @Path("/ranking")
    @ApiOperation(value = "Obtenir ranking")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Usuario.class, responseContainer = "List")
    })
    @Produces(MediaType.APPLICATION_JSON)

    public Response getRanking() {
        List<Usuario> ranking = shopManager.getRanking(); // manager/ShopManagerImpl.java, public List<Usuario> getRanking()

        // GenericEntity s’utilitza a la resposta REST quan vols enviar una llista (o col·lecció) d’objectes
        GenericEntity<List<Usuario>> entity = new GenericEntity<List<Usuario>>(ranking) {};

        return Response.status(200) // 200: public List<Usuario> getRanking() retorna la llista d'usuaris del ranking
                .entity(entity)
                .build();
        // android: RankingActivity, loadRanking(): onResponse() --> response.isSuccessful() && response.body() != null
    }


    // endpoint GET PERFIL USUARIO --> android(ShopService): @GET("/v1/shop/perfil/{username}")
    @GET
    @Path("/perfil/{username}")
    @ApiOperation(value = "Obtenir perfil d'usuari")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Usuario.class),
            @ApiResponse(code = 404, message = "Usuari no trobat", response = MessageResponse.class)
    })
    @Produces(MediaType.APPLICATION_JSON)

    public Response getPerfil(@PathParam("username") String username) {

        try {
            Usuario u = shopManager.getPerfil(username);
            return Response.status(200) // 200: public Usuario getPerfil(String username) retorna l'usuari
                    .entity(u)
                    .build();
            // android: ProfileActivity, loadUserProfile(): onResponse() --> response.isSuccessful() && response.body() != null

        } catch (RuntimeException e) {
            return Response.status(404) // 404: public Usuario getPerfil(String username) thow exception
                    .entity(new MessageResponse(e.getMessage()))
                    .build();
            // android: ProfileActivity, loadUserProfile(): onResponse() --> !response.isSuccessful()
        }
    }


    // endpoint GET INVENTARIO USUARIO --> android(ShopService): @GET("/v1/shop/inventario/{username}")
    @GET
    @Path("/inventario/{username}")
    @ApiOperation(value = "Obtenir inventari d'un usuari")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = ItemInventario.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Usuari no trobat", response = MessageResponse.class)
    })
    @Produces(MediaType.APPLICATION_JSON)

    public Response getInventario(@PathParam("username") String username) {

        try{
            List<ItemInventario> inventory = shopManager.getItemByUsuario(username); // manager/ShopManagerImpl.java,
                                                                                     // public List<ItemInventario> getItemByUsuario(String username)

            // GenericEntity s’utilitza a la resposta REST quan vols enviar una llista (o col·lecció) d’objectes
            // En aquest cas una llista de DTOs ItemInventario
            GenericEntity<List<ItemInventario>> entity = new GenericEntity<List<ItemInventario>>(inventory) {};
            return Response.status(200) // 200: public List<ItemInventario> getItemByUsuario(String username) retorna la llista d'items de l'inventari de l'usuari
                    .entity(entity)
                    .build();
            // android: InventoryActivity, loadInventario(): onResponse() --> response.isSuccessful()

        } catch(RuntimeException e){
            return Response.status(404) // 404: public List<ItemInventario> getItemByUsuario(String username) thow exception
                    .entity(new MessageResponse(e.getMessage()))
                    .build();
            // android: InventoryActivity, loadInventario(): onResponse() --> !response.isSuccessful()
        }
    }
}

