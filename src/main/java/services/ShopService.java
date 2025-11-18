package services;

import database.models.Item;
import database.models.Usuario;
import manager.ShopManagerImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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

    @GET
    @Path("/items")
    @ApiOperation(value = "Obtener todos los items de la tienda")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Item.class, responseContainer = "List")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems() {
        List<Item> items = shopManager.getItemsTienda();
        GenericEntity<List<Item>> entity = new GenericEntity<List<Item>>(items) {};
        return Response.status(200).entity(entity).build();
    }

    @POST
    @Path("/buy/{id}")
    @ApiOperation(
            value = "Comprar un item de la tienda",
            notes = "Permite a un usuario comprar un item específico por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Item comprado correctamente"),
            @ApiResponse(code = 404, message = "Item o usuario no encontrado", response = String.class),
            @ApiResponse(code = 500, message = "Error interno del servidor", response = String.class)
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response comprarItem(@PathParam("id") Integer itemId, String username) {
        try {
            if (username != null) {
                username = username.replace("\"", "").trim();
            }

            shopManager.comprarItem(username, itemId);
            return Response.status(Response.Status.OK)
                    .entity("{\"message\": \"Item comprado correctamente\"}")
                    .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    @GET
    @Path("/monedas/{username}")
    @ApiOperation(
            value = "Devuelve las monoedas que tiene un usuario",
            notes = "Devuelve las monedas que tiene un usuaria especificando su ursername"
    )
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCoins(@PathParam("username") String username) {

        username = username.replace("\"", "").trim();

        int monedas = this.shopManager.getMonedas(username);

        if (monedas < 0) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Monedas insuficientes de : " + username + "\"}" + monedas).build();
        }

        return Response.ok("{\"coins\":" + monedas + "}").build();
    }
    @GET
    @Path("/perfil/{username}")
    @ApiOperation(
            value = "Devuelve perfil usuario",
            notes = "Devuelve perfil usuario en funcion de id"
    )
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerfil(@PathParam("username") String usernameJson) {

        usernameJson = usernameJson.replace("\"", "").trim();
        Usuario u = shopManager.getPerfil(usernameJson);

        if (u == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Usuario no encontrado: " + usernameJson + "\"}")
                    .build();
        }

        String json = String.format(
                "{" +
                        "\"username\":\"%s\"," +
                        "\"nombre\":\"%s\"," +
                        "\"apellido\":\"%s\"," +
                        "\"email\":\"%s\"," +
                        "\"monedas\":%d," +
                        "\"mejorPuntuacion\":%d" +
                        "}",
                u.getUsername(),
                u.getNombre(),
                u.getApellido(),
                u.getGmail(),
                u.getMonedas(),
                u.getMejorPuntuacion()
        );

        return Response.ok(json).build();
    }
}

