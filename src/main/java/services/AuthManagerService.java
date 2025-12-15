package services;


import db.orm.model.Usuario;
import manager.AuthManagerImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.DTOs.MessageResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Api(value = "/auth", description = "Servicios de autenticación de usuarios")
@Path("/auth")
public class AuthManagerService {

    private final AuthManagerImpl am;

    public AuthManagerService() {
        this.am = AuthManagerImpl.getInstance();
    }

    // endpoint REGISTER --> android(AuthService): @POST("/v1/auth/register")
    @POST
    @Path("/register")
    @ApiOperation(
            value = "Registrar un nuevo usuario",
            notes = "Crea un nuevo usuario con nombre y contraseña."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Usuario registrado correctamente", response = Usuario.class),
            @ApiResponse(code = 400, message = "El usuario ya existe", response = MessageResponse.class)
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Response register(Usuario usuario) {
        try {
            am.register(usuario); // manager/AuthManagerImpl.java, public void register(Usuario usuario)

            return Response.status(Response.Status.CREATED) // 201: public void register(Usuario usuario) no retorna res (usuari resigtrat correctament)
                    .entity(usuario) // serialitza l'usuari registrat i l'envia a android (DTO implícit)
                    .build();
            // android: RegisterActivity, doRegister(): onResponse() --> response.isSuccessful()

        } catch (RuntimeException e) {

            return Response.status(Response.Status.BAD_REQUEST) // 400: public void register(Usuario usuario) thow exception
                    .entity(new MessageResponse(e.getMessage())) // serialitza el missatge d'error i l'envia a android
                    .build();
            // android: RegisterActivity, doRegister(): onResponse() --> !response.isSuccessful()
        }
    }

    // endpoint LOGIN --> android(AuthService): @POST("/v1/auth/login")
    @POST
    @Path("/login")
    @ApiOperation(
            value = "Iniciar sesión",
            notes = "Verifica las credenciales del usuario y devuelve sus datos si son correctas."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Inicio de sesión exitoso", response = Usuario.class),
            @ApiResponse(code = 401, message = "Usuario o contraseña incorrectos", response = MessageResponse.class)
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Response login(Usuario usuario) {
        try {
            Usuario u = am.login(usuario); // manager/AuthManagerImpl.java, public Usuario login(Usuario usuario)

            return Response.status(Response.Status.OK) // 200: public Usuario login(Usuario usuario) retorna l'usuari si les credencials son correctes
                    .entity(u)
                    .build();
            // android: LoginActivity, doLogin(): onResponse() --> response.isSuccessful()

        } catch (RuntimeException e) {

            return Response.status(Response.Status.UNAUTHORIZED) // 401: public Usuario login(Usuario usuario) thow exception
                    .entity(new MessageResponse(e.getMessage())) // serialitza el missatge d'error i l'envia a android
                    .build();
            // android: LoginActivity, doLogin(): onResponse() --> !response.isSuccessful()
        }
    }


    // COMPARAR ESTRUCTURA AMB EL GET ITEMS!!!!! PER FERLA MES CORRECTE
    @GET
    @Path("/users")
    @ApiOperation(
            value = "Obtener todos los usuarios registrados",
            notes = "Devuelve una lista de todos los usuarios en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lista de usuarios obtenida correctamente", response = Usuario.class, responseContainer = "List"),
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        List<Usuario> users = this.am.getRegisteredUsers();
        return Response.status(Response.Status.OK)
                .entity(users)
                .build();
    }
}
