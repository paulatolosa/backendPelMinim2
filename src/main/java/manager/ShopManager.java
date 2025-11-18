package manager;

import database.models.Item;
import database.models.Usuario;

import java.util.List;

public interface ShopManager {
    List<Item> getItemsTienda();
    void comprarItem(String username, Integer itemId);
    int getMonedas(String username);
    Usuario getPerfil(String username);
    List<Usuario> getRanking();
}

