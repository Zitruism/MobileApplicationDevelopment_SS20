package de.zitruism.thl_todo_liste.database.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class Contact {

    private String id;
    private String name;
    private String number;
    private Bitmap icon;
    private Uri iconURI;


    public Contact(String id, String name) {
        this.id = id;
        this.name = name;
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
