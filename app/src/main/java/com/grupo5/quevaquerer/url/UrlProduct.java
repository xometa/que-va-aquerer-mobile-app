package com.grupo5.quevaquerer.url;

import com.grupo5.quevaquerer.props.Constantes;

public class UrlProduct {
    public static String urlPhoto = Constantes.PHOTO;
    private String getListProducts = Constantes.URL+"RouteProduct.php?option=";
    private String getListAvailableProducts = Constantes.URL+"RouteProduct.php?option=";
    private String postStatusProduct = Constantes.URL+"RouteProduct.php";
    private String postProduct = Constantes.URL+"RouteProduct.php";
    private String deleteProduct = Constantes.URL+"RouteProduct.php?idproduct=";
    private String quantityAvailable = Constantes.URL+"RouteProduct.php?option=quantityAvailable&idproduct=";

    public UrlProduct() {
    }

    public String getListProducts(String option) {
        return getListProducts + option;
    }

    public String postStatusProduct() {
        return postStatusProduct;
    }

    public String postProduct() {
        return postProduct;
    }

    public String deleteProduct(int idproduct) {
        return deleteProduct + String.valueOf(idproduct);
    }

    public String getListAvailableProducts(String option) {
        return getListAvailableProducts + option;
    }

    public String getQuantityAvailable(int idproduct) {
        return quantityAvailable + String.valueOf(idproduct);
    }
}
