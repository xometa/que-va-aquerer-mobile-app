package com.grupo5.quevaquerer.props;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.fragment.app.FragmentActivity;

import com.grupo5.quevaquerer.entity.Product;
import com.grupo5.quevaquerer.user.HomeUserActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Props {
    public Props() {
    }

    public static String getPath(Uri imageUri, FragmentActivity activity) {
        Cursor c = activity.getContentResolver().query(imageUri, null, null, null, null);
        if (c == null) {
            return imageUri.getPath();
        } else {
            c.moveToFirst();
            int index = c.getColumnIndex(MediaStore.Images.Media.DATA);
            return c.getString(index);
        }
    }

    public static Date parseDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date f = null;
        try {
            f = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return f;
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    public static String formatDate1(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static boolean productOrderList(ArrayList<Product> list, Product product) {
        /**
         * Cuando el cliente seleccione un producto este metodo sera invocado,
         * el cual verificara que el producto no exista, para poder ser agregado,
         * si el producto no existe se retornara false, caso contrario se emitira
         * una alerta
         * */
        boolean exist = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == product.getId()) {
                exist = true;
            }
        }
        return exist;
    }

    public static String getLocationName(Context ctx, double la, double lo) {
        String location = "";
        if (la != 0) {
            Geocoder geocoder = new Geocoder(ctx);
            try {
                Address addresses = geocoder.getFromLocation(la, lo, 1).get(0);
                location = addresses.getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return location;
    }
}
