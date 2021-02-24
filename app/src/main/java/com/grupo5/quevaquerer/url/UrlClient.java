package com.grupo5.quevaquerer.url;

import com.grupo5.quevaquerer.props.Constantes;

public class UrlClient {
    public static String urlPhoto = Constantes.PHOTO;
    private String postClient = Constantes.URL + "RouteClient.php";
    private String getQuantityClient = Constantes.URL + "RouteClient.php?option=";
    private String getListClients = Constantes.URL + "RouteClient.php?option=";
    private String getClient = Constantes.URL + "RouteClient.php?option=retrievClient&idclient=";
    private String getAvailableUser = Constantes.URL + "RouteClient.php?option=availableUser";
    private String postUserPassword = Constantes.URL + "RouteClient.php";
    private String deleteClient = Constantes.URL + "RouteClient.php?idclient=";

    public UrlClient() {
    }

    public String postClient() {
        return postClient;
    }

    public String getQuantityClient(String option) {
        return getQuantityClient + option;
    }

    public String getListClients(String option) {
        return getListClients + option;
    }

    public String getClient(int idclient) {
        return getClient + String.valueOf(idclient);
    }

    public String getAvailableUser(String user, String password) {
        return getAvailableUser + "&username=" + user + "&userpassword=" + password;
    }

    public String postUserPassword() {
        return postUserPassword;
    }

    public String deleteClient(int idclient) {
        return deleteClient + String.valueOf(idclient);
    }
}
