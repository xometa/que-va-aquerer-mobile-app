package com.grupo5.quevaquerer.url;

import com.grupo5.quevaquerer.props.Constantes;

public class UrlOrder {
    public static String urlPhoto = Constantes.PHOTO;
    private String getHomeDetails = Constantes.URL + "RouteOrder.php?option=";
    private String getListOrderDay = Constantes.URL + "RouteOrder.php?option=";
    private String getListAllOrdes = Constantes.URL + "RouteOrder.php?option=";
    private String postSaveOrder = Constantes.URL + "RouteOrder.php";
    private String postStatusOrder = Constantes.URL + "RouteOrder.php";
    private String getMyOrderDay = Constantes.URL + "RouteOrder.php?option=myOrderDay&idclient=";

    public UrlOrder() {
    }

    public String getHomeDetails(String option) {
        return getHomeDetails + option;
    }

    public String getListOrderDay(String option) {
        return getListOrderDay + option;
    }

    public String getListAllOrdes(String option) {
        return getListAllOrdes + option;
    }

    public String postSaveOrder() {
        return postSaveOrder;
    }

    public String postStatusOrder() {
        return postStatusOrder;
    }

    public String getMyOrderDay(int idclient) {
        return getMyOrderDay + String.valueOf(idclient);
    }
}
