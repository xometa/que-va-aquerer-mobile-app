package com.grupo5.quevaquerer.slider;

public class Slider {
    private int Image;
    private String titleone;
    private String titletwo;

    public Slider() {
    }

    public Slider(int image, String titleone, String titletwo) {
        Image = image;
        this.titleone = titleone;
        this.titletwo = titletwo;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }

    public String getTitleone() {
        return titleone;
    }

    public void setTitleone(String titleone) {
        this.titleone = titleone;
    }

    public String getTitletwo() {
        return titletwo;
    }

    public void setTitletwo(String titletwo) {
        this.titletwo = titletwo;
    }
}
