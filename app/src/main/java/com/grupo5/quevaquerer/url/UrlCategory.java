package com.grupo5.quevaquerer.url;

import com.grupo5.quevaquerer.props.Constantes;

public class UrlCategory {
    public static String urlPhoto = Constantes.PHOTO;
    private String getListCategories = Constantes.URL+"RouteCategory.php?option=";
    private String getCategory = Constantes.URL+"RouteCategory.php?idcategory";
    private String postCategory = Constantes.URL+"RouteCategory.php";
    private String deleteCategory = Constantes.URL+"RouteCategory.php?idcategory=";

    public UrlCategory() {
    }

    public String getListCategories(String option) {
        return getListCategories + option;
    }

    public String getCategory(int idcategory) {
        return getCategory + String.valueOf(idcategory);
    }

    public String postCategory() {
        return postCategory;
    }

    public String deleteCategory(int idCategory) {
        return deleteCategory + String.valueOf(idCategory);
    }
}
