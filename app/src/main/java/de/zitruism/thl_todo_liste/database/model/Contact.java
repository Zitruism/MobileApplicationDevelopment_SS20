package de.zitruism.thl_todo_liste.database.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class Contact {

    private String id;
    private String name;
    private String number;
    private Bitmap icon;
    private Uri iconURI;


    public Contact(String id, String name, String number, Bitmap icon, Uri iconURI) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.icon = icon;
        this.iconURI = iconURI;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public Uri getIconURI() {
        return iconURI;
    }
}
